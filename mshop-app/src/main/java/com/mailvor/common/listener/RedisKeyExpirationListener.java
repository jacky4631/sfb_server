//package com.mailvor.common.listener;
//
//import com.mailvor.utils.RedisUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.stereotype.Component;
//
//import static com.mailvor.constant.ShopConstants.PID_TB_BINDING;
//
///**
// * The type Redis key expiration listener.
// */
//@Component
//@Slf4j
//public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
//
//    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
//        super(listenerContainer);
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        String expiredKey = message.toString(); // 获取过期的key
//        if (expiredKey.startsWith(PID_TB_BINDING)) { // 判断是否是想要监听的过期key
//            //监听pid绑定过期，重新放进redis list
//            log.info("redis key过期：{}", expiredKey);
//            String[] splitKey = expiredKey.split(":");
//            if(splitKey.length != 4) {
//                return;
//            }
//            String pid = splitKey[3];
//            RedisUtil.setPid(pid);
//        }
//
//    }
//}
//
