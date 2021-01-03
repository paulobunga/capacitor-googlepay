declare module '@capacitor/core' {
  interface PluginRegistry {
    IonicGooglePay: IonicGooglePayPlugin;
  }
}

export interface IonicGooglePayPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  isReadyToPay(): Promise<any>;

  requestPayment(paymentDetails: any): Promise<any>;
}
