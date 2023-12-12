// chose a values out of the `possibleValues` array using a hash of `data`
//
// for this we create a hash out data and take the first
export const chooseValueUsingHash = async <T>(
  possibleValues: T[],
  data: string,
): Promise<T> => {
  const encoder = new TextEncoder();
  const encodedData = encoder.encode(data);

  // hash the values and convert it to a hex string
  const hashBuffer = await crypto.subtle.digest("SHA-256", encodedData);
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  const hashHex = hashArray.map((byte) => byte.toString(16).padStart(2, "0")).join("");

  // convert fist 8 characters to a integer
  // and constraint to the size of the array
  const hashInt = parseInt(hashHex.substring(0, 8), 16);
  const index = hashInt % possibleValues.length;

  return possibleValues[index] as T;
};
