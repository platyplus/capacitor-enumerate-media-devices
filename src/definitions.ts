declare module '@capacitor/core' {
  interface PluginRegistry {
    EnumeratePlugin: EnumeratePluginPlugin
  }
}

export interface EnumeratePluginPlugin {
  enumerateDevices(): Promise<MediaDeviceInfo[]>
  echo(options: { value: string }): Promise<{ value: string }>
}
