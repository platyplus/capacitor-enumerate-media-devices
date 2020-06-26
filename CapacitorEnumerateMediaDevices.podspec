
  Pod::Spec.new do |s|
    s.name = 'CapacitorEnumerateMediaDevices'
    s.version = '0.0.1'
    s.summary = 'Wrapper to enumerate media devices in the navigator.mediaDevices.enumerateDevices fashion'
    s.license = 'MIT'
    s.homepage = 'https://github.com/platyplus/capacitor-enumerate-media-devices'
    s.author = ''
    s.source = { :git => 'https://github.com/platyplus/capacitor-enumerate-media-devices', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end