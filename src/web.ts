import { registerWebPlugin, WebPlugin } from '@capacitor/core';
import { IonicGooglePayPlugin } from './definitions';

export class IonicGooglePayWeb
  extends WebPlugin
  implements IonicGooglePayPlugin {
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

  async isReadyToPay() {}

  async requestPayment(paymentDetails: any): Promise<any> {
    return paymentDetails;
  }
}

const IonicGooglePay = new IonicGooglePayWeb();

export { IonicGooglePay };

registerWebPlugin(IonicGooglePay);
