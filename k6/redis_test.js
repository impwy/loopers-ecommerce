import { createOptions, requestProductList, verifyProductData } from './product_read_scenario.js';

export const options = createOptions('redis');

export function setup() {
  verifyProductData('redis');
}

export default function() {
  requestProductList('redis');
}
