import { WebPlugin } from '@capacitor/core';
import { IonicGooglePayPlugin } from './definitions';

export class IonicGooglePayWeb extends WebPlugin implements IonicGooglePayPlugin {
  constructor() {
    super({
      name: 'IonicGooglePay',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const IonicGooglePay = new IonicGooglePayWeb();

export { IonicGooglePay };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(IonicGooglePay);
