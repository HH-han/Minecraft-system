[CallPanel] Initializing WebRTC, isCaller: true, callType: voice, callId: 69beb550-034b-4b68-989c-26fbc218609f
CallPanel.vue:266 [CallPanel] Stream changed: local
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'call', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'call', data: {…}}
index.vue:474 [ImChat] Handling call request: {callId: '69beb550-034b-4b68-989c-26fbc218609f', receiverId: 19, sdpOffer: '{"sdp":"v=0\\r\\no=- 4763819949326817636 2 IN IP4 12…-6a26-48b4-9426-6d1e9ff691ef\\r\\n","type":"offer"}', callerId: 15, callType: 'voice', …}
index.vue:482 [ImChat] Looking for caller: 15 , friends count: 3 , currentUserId: 15
index.vue:485 [ImChat] All friends IDs: (3) [{…}, {…}, {…}]
index.vue:516 [ImChat] Caller not found in friends list: 15 type: number
（匿名） @ index.vue:516
（匿名） @ index.vue:465
（匿名） @ websocket.js:264
emit @ websocket.js:262
handleMessage @ websocket.js:117
（匿名） @ websocket.js:62
index.vue:551 [ImChat] Creating default caller for: 15


[WebSocket] Received message: {type: 'VOICE_CALL', channel: 'call', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'call', data: {…}}
index.vue:474 [ImChat] Handling call request: {callId: '69beb550-034b-4b68-989c-26fbc218609f', receiverId: 19, callerId: 15, callType: 'voice', status: 'initiated'}
index.vue:482 [ImChat] Looking for caller: 15 , friends count: 1 , currentUserId: 19
index.vue:485 [ImChat] All friends IDs: [{…}]
index.vue:510 [ImChat] Found caller: Proxy(Object) {id: 15, name: '爱吃薯条的小懒猪', avatar: 'http://localhost:8080/upload/382e6b20-cccc-4bc6-a48a-67b3769a5184.jpeg', lastMessage: '', time: '', …}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:1545720169 1 udp 212226022…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:844390870 1 udp 2122194687…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:4060293055 1 udp 212213171…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:940279956 1 udp 2122066175…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'sdp', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'sdp', data: {…}}
index.vue:568 [ImChat] Handling SDP message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', receiverId: 19, sdpOffer: '{"sdp":"v=0\\r\\no=- 4763819949326817636 2 IN IP4 12…-6a26-48b4-9426-6d1e9ff691ef\\r\\n","type":"offer"}', callerId: 15, callType: 'voice', …}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:3460335847 1 udp 212199807…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:1187277324 1 tcp 151808639…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:261021613 1 udp 1685790463…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:2962188927 1 tcp 151801830…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:586036209 1 tcp 1518280447…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:2362230055 1 tcp 151815193…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
websocket.js:61 [WebSocket] Received message: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:454 [ImChat] Call message received: {type: 'VOICE_CALL', channel: 'ice', data: {…}}
index.vue:589 [ImChat] Handling ICE message: {callId: '69beb550-034b-4b68-989c-26fbc218609f', candidate: '{"candidate":"candidate:1285260110 1 tcp 151821491…:"0","sdpMLineIndex":0,"usernameFragment":"El4+"}'}
CallPanel.vue:161 [CallPanel] Accepting voice call, callId: 69beb550-034b-4b68-989c-26fbc218609f
CallPanel.vue:260 [CallPanel] Initializing WebRTC, isCaller: false, callType: voice, callId: 69beb550-034b-4b68-989c-26fbc218609f
CallPanel.vue:266 [CallPanel] Stream changed: local