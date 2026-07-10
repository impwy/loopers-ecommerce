import { createOptions, requestProductList, verifyProductData } from './product_read_scenario.js';

export const options = createOptions('denormalize');

export function setup() {
  verifyProductData('denormalize');
}

export default function() {
  requestProductList('denormalize');
}
