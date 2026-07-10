import { createOptions, requestProductList, verifyProductData } from './product_read_scenario.js';

export const options = createOptions('normalize');

export function setup() {
  verifyProductData('normalize');
}

export default function() {
  requestProductList('normalize');
}
