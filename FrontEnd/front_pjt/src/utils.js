export function formatTime(dateTimeString) {
  if (!dateTimeString) return '';

  const date = new Date(dateTimeString);
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  
  return `${hours}:${minutes}`;
}

export function handleClickOutside(elementRef, callback) {
  return (event) => {
    if(elementRef == null) return
    const element  = elementRef instanceof HTMLElement ? elementRef : elementRef.value;
    if (element instanceof HTMLElement &&
      !element.contains(event.target)) {
      callback();
    }
  };
}