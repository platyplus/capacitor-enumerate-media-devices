declare module '@capacitor/core' {
  interface PluginRegistry {
    EnumeratePlugin: EnumeratePluginPlugin
  }
}

export interface EnumeratePluginPlugin {
  enumerateDevices(): Promise<{ devices: MediaDeviceInfo[] }>
  echo(options: { value: string }): Promise<{ value: string }>
}
