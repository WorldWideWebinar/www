import { ref, onMounted, onUnmounted } from 'vue';

const clickOutsideDirective = {
  beforeMount(el, binding) {
    el.clickOutsideEvent = (event) => {
      if (!(el === event.target || el.contains(event.target))) {
        // 바인딩된 메서드를 호출
        binding.value(event);
      }
    };
    document.addEventListener('click', el.clickOutsideEvent);
  },
  unmounted(el) {
    document.removeEventListener('click', el.clickOutsideEvent);
  },
};

export default clickOutsideDirective;