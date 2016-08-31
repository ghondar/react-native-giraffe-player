var { DeviceEventEmitter, NativeModules } = require('react-native')
var { GPlayerAndroid } = NativeModules

var GIRAFFE_PLAYER_EVENTS = {
  onBufferingStart              : 'onBufferingStart',
  onBufferingEnd                : 'onBufferingEnd',
  onNetworkBandwidth            : 'onNetworkBandwidth',
  onRenderingStart              : 'onRenderingStart',
  onControlPanelVisibilityChange: 'onRenderingStart',
  onComplete                    : 'onComplete',
  onError                       : 'onError'
}

var _GiraffePlayerHandlers = {}

var GPlayer = {
  addEventListener   : function(type, handler) {
    _GiraffePlayerHandlers[ handler ] = DeviceEventEmitter.addListener(
      GIRAFFE_PLAYER_EVENTS[ type ],
      (giraffePlayerData) => {
        handler(giraffePlayerData)
      }
    )
  },
  removeEventListener: function(type, handler) {
    if (!_GiraffePlayerHandlers[ handler ]) {
      return
    }
    _GiraffePlayerHandlers[ handler ].remove()
    _GiraffePlayerHandlers[ handler ] = null
  },
  ...GPlayerAndroid
}

module.exports = GPlayer
