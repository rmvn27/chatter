// check if the other day happened today
//
// src: https://flaviocopes.com/how-to-determine-date-is-today-javascript/
export const isToday = (today: Date, other: Date) => {
  return (
    other.getDate() == today.getDate() &&
    other.getMonth() == today.getMonth() &&
    other.getFullYear() == today.getFullYear()
  );
};
