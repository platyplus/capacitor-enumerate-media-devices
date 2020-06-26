import { WebPlugin } from '@capacitor/core'
import { EnumeratePluginPlugin } from './definitions'

export class EnumeratePluginWeb extends WebPlugin
  implements EnumeratePluginPlugin {
  constructor() {
    super({
      name: 'EnumeratePlugin',
      platforms: ['web'],
    })
  }

  async enumerateDevices(): Promise<{ devices: MediaDeviceInfo[] }> {
    const devices = await navigator.mediaDevices.enumerateDevices()
    return { devices }
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options)
    return options
  }
}

const EnumeratePlugin = new EnumeratePluginWeb()

export { EnumeratePlugin }

import { registerWebPlugin } from '@capacitor/core'
registerWebPlugin(EnumeratePlugin)
