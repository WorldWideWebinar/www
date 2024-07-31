<template>
  <div v-if="visible" class="alert" :class="type">
    <span class="closebtn" @click="close">&times;</span>
    {{ message }}
  </div>
</template>

<script>
export default {
  props: {
    type: {
      type: String,
      default: 'error' // 기본 타입은 'error'로 설정
    },
    message: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      visible: true
    };
  },
  methods: {
    close() {
      this.visible = false;
    }
  }
};
</script>

<style scoped>
.alert {
  padding: 20px;
  background-color: #f44336; /* Red */
  color: white;
  margin-bottom: 15px;
}

.closebtn {
  margin-left: 15px;
  color: white;
  font-weight: bold;
  float: right;
  font-size: 22px;
  line-height: 20px;
  cursor: pointer;
  transition: 0.3s;
}

.closebtn:hover {
  color: black;
}
</style>