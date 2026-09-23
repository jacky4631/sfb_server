# API接口说明文档（自动扫描）

- 生成时间：2026-04-06 14:02:16
- 扫描范围：`mshop-*/src/main/java/**/*Controller.java`
- 接口类总数：**117**
- 接口条目总数：**661**

> 说明：该清单按控制器注解自动提取，动态路由或条件映射需人工复核。

## 模块索引
- `mshop-admin`：13 个接口类
- `mshop-app`：43 个接口类
- `mshop-gen`：2 个接口类
- `mshop-log`：1 个接口类
- `mshop-mp`：1 个接口类
- `mshop-shop`：49 个接口类
- `mshop-store`：1 个接口类
- `mshop-tool`：7 个接口类

## 模块：`mshop-admin`

### `LimitController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/monitor/rest/LimitController.java`
- Java包：`com.mailvor.modules.monitor.rest`
- 基础路径：`/api/limit`
- 接口列表：
  - `GET` `/api/limit` -> `testLimit`
    - 功能：测试

### `RedisController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/monitor/rest/RedisController.java`
- Java包：`com.mailvor.modules.monitor.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/redis` -> `getRedis`
    - 功能：方法 `getRedis`（未标注ApiOperation）
  - `DELETE` `/api/redis` -> `delete`
    - 功能：方法 `delete`（未标注ApiOperation）
  - `DELETE` `/api/redis/all` -> `deleteAll`
    - 功能：方法 `deleteAll`（未标注ApiOperation）

### `VisitsController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/monitor/rest/VisitsController.java`
- Java包：`com.mailvor.modules.monitor.rest`
- 基础路径：`/api/visits`
- 接口列表：
  - `POST` `/api/visits` -> `create`
    - 功能：创建访问记录
  - `GET` `/api/visits` -> `get`
    - 功能：查询
  - `GET` `/api/visits/chartData` -> `getChartData`
    - 功能：查询图表数据

### `QuartzJobController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/quartz/rest/QuartzJobController.java`
- Java包：`com.mailvor.modules.quartz.rest`
- 基础路径：`/api/jobs`
- 接口列表：
  - `GET` `/api/jobs` -> `getJobs`
    - 功能：查询定时任务
  - `GET` `/api/jobs/download` -> `download`
    - 功能：导出任务数据
  - `GET` `/api/jobs/logs/download` -> `downloadLog`
    - 功能：导出日志数据
  - `GET` `/api/jobs/logs` -> `getJobLogs`
    - 功能：查询任务执行日志
  - `POST` `/api/jobs` -> `create`
    - 功能：新增定时任务
  - `PUT` `/api/jobs` -> `update`
    - 功能：修改定时任务
  - `PUT` `/api/jobs/{id}` -> `updateIsPause`
    - 功能：更改定时任务状态
  - `PUT` `/api/jobs/exec/{id}` -> `execution`
    - 功能：执行定时任务
  - `DELETE` `/api/jobs` -> `delete`
    - 功能：删除定时任务

### `AuthController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/security/rest/AuthController.java`
- Java包：`com.mailvor.modules.security.rest`
- 基础路径：`/auth`
- 接口列表：
  - `POST` `/auth/login` -> `login`
    - 功能：登录授权
  - `GET` `/auth/info` -> `getUserInfo`
    - 功能：获取用户信息
  - `GET` `/auth/code` -> `getCode`
    - 功能：获取验证码
  - `DELETE` `/auth/logout` -> `logout`
    - 功能：退出登录

### `OnlineController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/security/rest/OnlineController.java`
- Java包：`com.mailvor.modules.security.rest`
- 基础路径：`/auth/online`
- 接口列表：
  - `GET` `/auth/online` -> `getAll`
    - 功能：查询在线用户
  - `GET` `/auth/online/download` -> `download`
    - 功能：导出数据
  - `DELETE` `/auth/online` -> `delete`
    - 功能：踢出用户
  - `POST` `/auth/online/delete` -> `deletet`
    - 功能：踢出移动端用户
  - `POST` `/auth/online/deletes` -> `deleteOneMonth`
    - 功能：踢出一个月未登录的用户

### `DeptController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/DeptController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/dept`
- 接口列表：
  - `GET` `/api/dept/download` -> `download`
    - 功能：导出部门数据
  - `GET` `/api/dept` -> `getDepts`
    - 功能：查询部门
  - `POST` `/api/dept` -> `create`
    - 功能：新增部门
  - `PUT` `/api/dept` -> `update`
    - 功能：修改部门
  - `DELETE` `/api/dept` -> `delete`
    - 功能：删除部门

### `DictController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/DictController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/dict`
- 接口列表：
  - `GET` `/api/dict/download` -> `download`
    - 功能：导出字典数据
  - `GET` `/api/dict/all` -> `all`
    - 功能：查询字典
  - `GET` `/api/dict` -> `getDicts`
    - 功能：查询字典
  - `POST` `/api/dict` -> `create`
    - 功能：新增字典
  - `PUT` `/api/dict` -> `update`
    - 功能：修改字典
  - `DELETE` `/api/dict/{id}` -> `delete`
    - 功能：删除字典

### `DictDetailController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/DictDetailController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/dictDetail`
- 接口列表：
  - `GET` `/api/dictDetail` -> `getDictDetails`
    - 功能：查询字典详情
  - `GET` `/api/dictDetail/map` -> `getDictDetailMaps`
    - 功能：查询多个字典详情
  - `POST` `/api/dictDetail` -> `create`
    - 功能：新增字典详情
  - `PUT` `/api/dictDetail` -> `update`
    - 功能：修改字典详情
  - `DELETE` `/api/dictDetail/{id}` -> `delete`
    - 功能：删除字典详情

### `JobController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/JobController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/job`
- 接口列表：
  - `GET` `/api/job/download` -> `download`
    - 功能：导出岗位数据
  - `GET` `/api/job` -> `getJobs`
    - 功能：查询岗位
  - `POST` `/api/job` -> `create`
    - 功能：新增岗位
  - `PUT` `/api/job` -> `update`
    - 功能：修改岗位
  - `DELETE` `/api/job` -> `delete`
    - 功能：删除岗位

### `MenuController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/MenuController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/menus`
- 接口列表：
  - `GET` `/api/menus/download` -> `download`
    - 功能：导出菜单数据
  - `GET` `/api/menus/build` -> `buildMenus`
    - 功能：获取前端所需菜单
  - `GET` `/api/menus/tree` -> `getMenuTree`
    - 功能：返回全部的菜单
  - `GET` `/api/menus` -> `getMenus`
    - 功能：查询菜单
  - `POST` `/api/menus` -> `create`
    - 功能：新增菜单
  - `PUT` `/api/menus` -> `update`
    - 功能：修改菜单
  - `DELETE` `/api/menus` -> `delete`
    - 功能：删除菜单

### `RoleController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/RoleController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/roles`
- 接口列表：
  - `GET` `/api/roles/{id}` -> `getRoles`
    - 功能：获取单个role
  - `GET` `/api/roles/download` -> `download`
    - 功能：导出角色数据
  - `GET` `/api/roles/all` -> `getAll`
    - 功能：返回全部的角色
  - `GET` `/api/roles` -> `getRoles`
    - 功能：查询角色
  - `GET` `/api/roles/level` -> `getLevel`
    - 功能：获取用户级别
  - `POST` `/api/roles` -> `create`
    - 功能：新增角色
  - `PUT` `/api/roles` -> `update`
    - 功能：修改角色
  - `PUT` `/api/roles/menu` -> `updateMenu`
    - 功能：修改角色菜单
  - `DELETE` `/api/roles` -> `delete`
    - 功能：删除角色

### `SysUserController`
- 类路径：`mshop-admin/src/main/java/com/mailvor/modules/system/rest/SysUserController.java`
- Java包：`com.mailvor.modules.system.rest`
- 基础路径：`/api/users`
- 接口列表：
  - `GET` `/api/users/download` -> `download`
    - 功能：导出用户数据
  - `GET` `/api/users` -> `getUsers`
    - 功能：查询用户
  - `POST` `/api/users` -> `create`
    - 功能：新增用户
  - `PUT` `/api/users` -> `update`
    - 功能：修改用户
  - `PUT` `/api/users/center` -> `center`
    - 功能：修改用户：个人中心
  - `DELETE` `/api/users` -> `delete`
    - 功能：删除用户
  - `POST` `/api/users/updatePass` -> `updatePass`
    - 功能：修改密码
  - `POST` `/api/users/updateAvatar` -> `updateAvatar`
    - 功能：修改头像
  - `POST` `/api/users/updateEmail/{code}` -> `updateEmail`
    - 功能：修改邮箱

## 模块：`mshop-app`

### `StoreBargainController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/activity/rest/StoreBargainController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/bargain/list` -> `getMwStoreBargainPageList`
    - 功能：砍价产品列表
  - `GET` `/bargain/detail/{id}` -> `getMwStoreBargain`
    - 功能：砍价详情
  - `POST` `/bargain/help/count` -> `helpCount`
    - 功能：砍价详情统计
  - `POST` `/bargain/share` -> `topCount`
    - 功能：砍价顶部统计
  - `POST` `/bargain/start` -> `start`
    - 功能：参与砍价
  - `POST` `/bargain/help` -> `help`
    - 功能：帮助好友砍价
  - `POST` `/bargain/help/price` -> `price`
    - 功能：获取砍掉金额
  - `POST` `/bargain/help/list` -> `helpList`
    - 功能：好友帮
  - `POST` `/bargain/start/user` -> `startUser`
    - 功能：获取开启砍价用户信息
  - `POST` `/bargain/poster` -> `poster`
    - 功能：砍价海报
  - `GET` `/bargain/user/list` -> `bargainUserList`
    - 功能：方法 `bargainUserList`（未标注ApiOperation）
  - `POST` `/bargain/user/cancel` -> `bargainCancel`
    - 功能：砍价取消

### `StoreCombinationController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/activity/rest/StoreCombinationController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/combination/list` -> `getList`
    - 功能：拼团产品列表
  - `GET` `/combination/detail/{id}` -> `detail`
    - 功能：拼团产品详情
  - `GET` `/combination/pink/{id}` -> `pink`
    - 功能：拼团明细
  - `POST` `/combination/poster` -> `poster`
    - 功能：拼团海报
  - `POST` `/combination/remove` -> `remove`
    - 功能：取消开团

### `StoreIntegralController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/activity/rest/StoreIntegralController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/products/integral` -> `goodsList`
    - 功能：获取积分产品列表

### `StoreSeckillController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/activity/rest/StoreSeckillController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/seckill/list/{time}` -> `getMwStoreSeckillPageList`
    - 功能：秒杀产品列表
  - `GET` `/seckill/detail/{id}` -> `getMwStoreSeckill`
    - 功能：秒杀产品详情
  - `GET` `/seckill/index` -> `getMwStoreSeckillIndex`
    - 功能：秒杀产品时间区间

### `AuthController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/auth/rest/AuthController.java`
- Java包：`com.mailvor.modules.auth.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/wxapp/authPhone/{phone}` -> `authPhone`
    - 功能：根据手机号查询用户状态
  - `POST` `/login` -> `login`
    - 功能：H5登录授权
  - `POST` `/login/mobile` -> `loginVerify`
    - 功能：APP验证码登录
  - `POST` `/login/shanyan` -> `shanyanLogin`
    - 功能：闪验手机号一键登录
  - `POST` `/register/reset` -> `updatePassword`
    - 功能：修改密码
  - `POST` `/register` -> `register`
    - 功能：H5/APP注册新用户
  - `POST` `/register/verify` -> `mobileVerify`
    - 功能：短信验证码发送
  - `POST` `/auth/logout` -> `logout`
    - 功能：退出登录
  - `GET` `/wechat/app/auth` -> `appAuthLogin`
    - 功能：微信APP登录
  - `POST` `/apple/app/auth` -> `appleLogin`
    - 功能：苹果授权
  - `GET` `/alipay/app/binding` -> `alipayAppAuthLogin`
    - 功能：支付宝授权
  - `GET` `/alipay/app/code` -> `alipayAppAuthCode`
    - 功能：app授权
  - `GET` `/wechat/app/binding` -> `wechatBinding`
    - 功能：微信绑定
  - `GET` `/wechat/auth` -> `authLogin`
    - 功能：微信公众号登录

### `StoreCartController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/cart/rest/StoreCartController.java`
- Java包：`com.mailvor.modules.cart.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/cart/count` -> `count`
    - 功能：获取数量
  - `POST` `/cart/add` -> `add`
    - 功能：添加购物车
  - `GET` `/cart/list` -> `getList`
    - 功能：购物车列表
  - `POST` `/cart/num` -> `cartNum`
    - 功能：修改产品数量
  - `POST` `/cart/del` -> `cartDel`
    - 功能：购物车删除产品

### `CouponController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/coupon/rest/CouponController.java`
- Java包：`com.mailvor.modules.coupon.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/coupon/receive` -> `receive`
    - 功能：领取优惠券
  - `GET` `/coupons/user/{type}` -> `getUserList`
    - 功能：用户已领取优惠券
  - `GET` `/coupons/user/pc/{type}` -> `getUserPCList`
    - 功能：用户已领取优惠券pc
  - `GET` `/coupons/order/{cartIds}` -> `orderCoupon`
    - 功能：优惠券订单获取

### `DataokeCmsController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/DataokeCmsController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/cms`
- 接口列表：
  - `GET` `/cms/brand/list` -> `brandList`
    - 功能：方法 `brandList`（未标注ApiOperation）
  - `GET` `/cms/hot` -> `hot`
    - 功能：方法 `hot`（未标注ApiOperation）
  - `GET` `/cms/ddq` -> `ddq`
    - 功能：方法 `ddq`（未标注ApiOperation）
  - `GET` `/cms/everyone/buy` -> `everyoneBuy`
    - 功能：方法 `everyoneBuy`（未标注ApiOperation）
  - `GET` `/cms/hot-words` -> `hotWords`
    - 功能：方法 `hotWords`（未标注ApiOperation）
  - `GET` `/cms/ranking/cate` -> `rankingCate`
    - 功能：方法 `rankingCate`（未标注ApiOperation）
  - `GET` `/cms/big/list` -> `goodsBigList`
    - 功能：方法 `goodsBigList`（未标注ApiOperation）
  - `GET` `/cms/cate` -> `pickCate`
    - 功能：方法 `pickCate`（未标注ApiOperation）
  - `GET` `/cms/check/cate` -> `checkCate`
    - 功能：方法 `checkCate`（未标注ApiOperation）
  - `GET` `/cms/brand/goods/list` -> `brandGoodsList`
    - 功能：方法 `brandGoodsList`（未标注ApiOperation）
  - `GET` `/cms/nine/cate` -> `nineCate`
    - 功能：方法 `nineCate`（未标注ApiOperation）
  - `GET` `/cms/nine/top` -> `nineTOP`
    - 功能：方法 `nineTOP`（未标注ApiOperation）
  - `GET` `/cms/nine/list` -> `nineList`
    - 功能：方法 `nineList`（未标注ApiOperation）

### `DataokeController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/DataokeController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/tao`
- 接口列表：
  - `GET` `/tao/home/url` -> `getUrl`
    - 功能：方法 `getUrl`（未标注ApiOperation）
  - `GET` `/tao/goods/list` -> `getGoodList`
    - 功能：方法 `getGoodList`（未标注ApiOperation）
  - `GET` `/tao/goods/search` -> `goodsSearch`
    - 功能：方法 `goodsSearch`（未标注ApiOperation）
  - `GET` `/tao/goods/detail` -> `getGoodsDetail`
    - 功能：方法 `getGoodsDetail`（未标注ApiOperation）
  - `GET` `/tao/goods/comment/list` -> `getCommentList`
    - 功能：方法 `getCommentList`（未标注ApiOperation）
  - `GET` `/tao/goods/word` -> `goodsWord`
    - 功能：方法 `goodsWord`（未标注ApiOperation）
  - `GET` `/tao/goods/parse` -> `goodsParse`
    - 功能：方法 `goodsParse`（未标注ApiOperation）
  - `GET` `/tao/goods/category` -> `getCategory`
    - 功能：方法 `getCategory`（未标注ApiOperation）
  - `GET` `/tao/topic/list` -> `getTopic`
    - 功能：方法 `getTopic`（未标注ApiOperation）
  - `GET` `/tao/banner/list` -> `getBanner`
    - 功能：方法 `getBanner`（未标注ApiOperation）
  - `GET` `/tao/activity/list` -> `getTbActivityList`
    - 功能：方法 `getTbActivityList`（未标注ApiOperation）
  - `GET` `/tao/activity/parse` -> `parseTbActivityList`
    - 功能：方法 `parseTbActivityList`（未标注ApiOperation）
  - `POST` `/tao/parse/content` -> `parseContentPost`
    - 功能：方法 `parseContentPost`（未标注ApiOperation）
  - `GET` `/tao/goods/similar/list` -> `getGoodSimilarList`
    - 功能：方法 `getGoodSimilarList`（未标注ApiOperation）
  - `GET` `/tao/ddq` -> `ddq`
    - 功能：方法 `ddq`（未标注ApiOperation）
  - `GET` `/tao/ranking/list` -> `rankingList`
    - 功能：方法 `rankingList`（未标注ApiOperation）
  - `GET` `/tao/shop/convert` -> `shopConvert`
    - 功能：方法 `shopConvert`（未标注ApiOperation）
  - `GET` `/tao/brand/list` -> `getBrandList`
    - 功能：方法 `getBrandList`（未标注ApiOperation）
  - `GET` `/tao/brand/goods/list` -> `getBrandGoodsList`
    - 功能：方法 `getBrandGoodsList`（未标注ApiOperation）
  - `GET` `/tao/tlj/goods/list` -> `getZeroGoodList`
    - 功能：方法 `getZeroGoodList`（未标注ApiOperation）
  - `GET` `/tao/tlj/goods/word` -> `tljGoodsWord`
    - 功能：方法 `tljGoodsWord`（未标注ApiOperation）
  - `GET` `/tao/tlj/use` -> `getTljUse`
    - 功能：方法 `getTljUse`（未标注ApiOperation）

### `DataokeDyController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/DataokeDyController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/dy`
- 接口列表：
  - `GET` `/dy/goods/search` -> `getGoodsSearch`
    - 功能：方法 `getGoodsSearch`（未标注ApiOperation）
  - `GET` `/dy/goods/detail` -> `getGoodsDetail`
    - 功能：方法 `getGoodsDetail`（未标注ApiOperation）
  - `GET` `/dy/word` -> `dyWord`
    - 功能：方法 `dyWord`（未标注ApiOperation）

### `DataokeJDController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/DataokeJDController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/jd`
- 接口列表：
  - `GET` `/jd/goods/detail` -> `getGoodsDetail`
    - 功能：方法 `getGoodsDetail`（未标注ApiOperation）
  - `GET` `/jd/goods/word` -> `goodsWord2`
    - 功能：方法 `goodsWord2`（未标注ApiOperation）
  - `GET` `/jd/rank/list` -> `getRankList`
    - 功能：方法 `getRankList`（未标注ApiOperation）

### `DataokePDDController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/DataokePDDController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/pdd`
- 接口列表：
  - `GET` `/pdd/goods/list` -> `getGoodList`
    - 功能：方法 `getGoodList`（未标注ApiOperation）
  - `GET` `/pdd/goods/cate` -> `getGoodList`
    - 功能：方法 `getGoodList`（未标注ApiOperation）
  - `GET` `/pdd/goods/detail` -> `getGoodsDetail`
    - 功能：方法 `getGoodsDetail`（未标注ApiOperation）
  - `GET` `/pdd/auth/query` -> `authQuery`
    - 功能：方法 `authQuery`（未标注ApiOperation）
  - `GET` `/pdd/auth` -> `auth`
    - 功能：方法 `auth`（未标注ApiOperation）
  - `GET` `/pdd/goods/word` -> `goodsWord`
    - 功能：方法 `goodsWord`（未标注ApiOperation）

### `DataokeVipController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/DataokeVipController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/vip`
- 接口列表：
  - `GET` `/vip/goods/list` -> `getGoodList`
    - 功能：方法 `getGoodList`（未标注ApiOperation）
  - `GET` `/vip/goods/search` -> `getGoodsSearch`
    - 功能：方法 `getGoodsSearch`（未标注ApiOperation）
  - `GET` `/vip/goods/detail` -> `getGoodsDetail`
    - 功能：方法 `getGoodsDetail`（未标注ApiOperation）
  - `GET` `/vip/goods/word` -> `goodsWord`
    - 功能：方法 `goodsWord`（未标注ApiOperation）

### `HaodankuController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/HaodankuController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/ku`
- 接口列表：
  - `GET` `/ku/pdd/goods/word` -> `getPDDWord`
    - 功能：方法 `getPDDWord`（未标注ApiOperation）
  - `GET` `/ku/shortLink` -> `getShortLink`
    - 功能：方法 `getShortLink`（未标注ApiOperation）
  - `GET` `/ku/pdd/nav` -> `pddNav`
    - 功能：方法 `pddNav`（未标注ApiOperation）
  - `GET` `/ku/pdd/cate` -> `pddCate`
    - 功能：方法 `pddCate`（未标注ApiOperation）
  - `GET` `/ku/pdd/list` -> `pddList`
    - 功能：方法 `pddList`（未标注ApiOperation）
  - `GET` `/ku/dy/nav` -> `dyNav`
    - 功能：方法 `dyNav`（未标注ApiOperation）
  - `GET` `/ku/dy/cate` -> `dyCate`
    - 功能：方法 `dyCate`（未标注ApiOperation）
  - `GET` `/ku/dy/list` -> `dyList`
    - 功能：方法 `dyList`（未标注ApiOperation）
  - `GET` `/ku/pick/cate` -> `pickCate`
    - 功能：方法 `pickCate`（未标注ApiOperation）
  - `GET` `/ku/pick/list` -> `pickList`
    - 功能：方法 `pickList`（未标注ApiOperation）
  - `GET` `/ku/mini/list` -> `miniList`
    - 功能：方法 `miniList`（未标注ApiOperation）
  - `GET` `/ku/banner` -> `banner`
    - 功能：方法 `banner`（未标注ApiOperation）
  - `GET` `/ku/banners` -> `banners`
    - 功能：方法 `banners`（未标注ApiOperation）
  - `GET` `/ku/tiles` -> `tiles`
    - 功能：方法 `tiles`（未标注ApiOperation）
  - `GET` `/ku/custom/cate` -> `customCate`
    - 功能：方法 `customCate`（未标注ApiOperation）
  - `GET` `/ku/custom/list` -> `customList`
    - 功能：方法 `customList`（未标注ApiOperation）
  - `GET` `/ku/dy/banner/word` -> `getDyWord`
    - 功能：方法 `getDyWord`（未标注ApiOperation）
  - `GET` `/ku/activity/detail` -> `parseMeeting`
    - 功能：方法 `parseMeeting`（未标注ApiOperation）
  - `GET` `/ku/hot/words` -> `hotWords`
    - 功能：方法 `hotWords`（未标注ApiOperation）
  - `GET` `/ku/dy/product/cate` -> `dyProductCate`
    - 功能：方法 `dyProductCate`（未标注ApiOperation）
  - `GET` `/ku/dy/product/list` -> `dyProductList`
    - 功能：方法 `dyProductList`（未标注ApiOperation）
  - `GET` `/ku/dy/product/detail` -> `dyProductDetail`
    - 功能：方法 `dyProductDetail`（未标注ApiOperation）
  - `GET` `/ku/dy/product/word` -> `dyProductWord`
    - 功能：方法 `dyProductWord`（未标注ApiOperation）
  - `GET` `/ku/makeOrder` -> `getCoudanList`
    - 功能：方法 `getCoudanList`（未标注ApiOperation）
  - `GET` `/ku/jd/goods/detail` -> `getGoodsDetail`
    - 功能：方法 `getGoodsDetail`（未标注ApiOperation）

### `HaodankuWaimaiController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/HaodankuWaimaiController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/ku`
- 接口列表：
  - `GET` `/ku/waimai/list` -> `meiTuanActivityList`
    - 功能：方法 `meiTuanActivityList`（未标注ApiOperation）
  - `GET` `/ku/waimai/word` -> `meiTuanWord`
    - 功能：方法 `meiTuanWord`（未标注ApiOperation）
  - `GET` `/ku/ele/list` -> `eleActivityList`
    - 功能：方法 `eleActivityList`（未标注ApiOperation）

### `LocalLifeController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/dataoke/rest/LocalLifeController.java`
- Java包：`com.mailvor.modules.dataoke.rest`
- 基础路径：`/ku`
- 接口列表：
  - `GET` `/ku/locallife` -> `localLife`
    - 功能：本地生活列表
  - `GET` `/ku/locallife/order` -> `localLifeOrder`
    - 功能：本地生活订单采集
  - `GET` `/ku/dy/life/getcity` -> `getCity`
    - 功能：抖音本地生活：获取城市
  - `GET` `/ku/dy/life/goods/list` -> `dyLifeList`
    - 功能：抖音本地生活：商品列表
  - `GET` `/ku/dy/life/city/list` -> `dyLifeCityList`
    - 功能：抖音本地生活：城市列表
  - `GET` `/ku/dy/life/category/list` -> `dyLifeCategoryList`
    - 功能：抖音本地生活：分类
  - `GET` `/ku/dy/life/goods/word` -> `dyLife`
    - 功能：抖音本地生活：转链

### `ShoperController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/manage/rest/ShoperController.java`
- Java包：`com.mailvor.modules.manage.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/admin/order/statistics` -> `statistics`
    - 功能：订单数据统计
  - `GET` `/admin/order/data` -> `data`
    - 功能：订单每月统计数据
  - `GET` `/admin/order/list` -> `orderList`
    - 功能：订单列表
  - `GET` `/admin/order/detail/{key}` -> `orderDetail`
    - 功能：订单详情
  - `POST` `/admin/order/price` -> `orderPrice`
    - 功能：订单改价
  - `GET` `/logistics` -> `express`
    - 功能：快递公司
  - `POST` `/admin/order/delivery/keep` -> `orderDelivery`
    - 功能：订单发货
  - `POST` `/admin/order/refund` -> `orderRefund`
    - 功能：订单退款
  - `GET` `/admin/order/time` -> `chartCount`
    - 功能：chart统计

### `PointsOrderController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/order/rest/PointsOrderController.java`
- Java包：`com.mailvor.modules.order.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/integral/confirm` -> `confirm`
    - 功能：积分确认

### `StoreOrderController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/order/rest/StoreOrderController.java`
- Java包：`com.mailvor.modules.order.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/order/confirm` -> `confirm`
    - 功能：订单确认
  - `POST` `/order/create` -> `create`
    - 功能：订单创建
  - `POST` `/order/pay` -> `pay`
    - 功能：订单支付
  - `GET` `/order/list` -> `orderList`
    - 功能：订单列表
  - `GET` `/order/detail/{key}` -> `detail`
    - 功能：订单详情
  - `POST` `/order/take` -> `orderTake`
    - 功能：订单收货
  - `POST` `/order/product` -> `product`
    - 功能：订单产品信息
  - `POST` `/order/comment` -> `comment`
    - 功能：订单评价
  - `POST` `/order/comments` -> `comments`
    - 功能：订单评价
  - `POST` `/order/del` -> `orderDel`
    - 功能：订单删除
  - `GET` `/order/refund/reason` -> `refundReason`
    - 功能：订单退款理由
  - `POST` `/order/refund/verify` -> `refundVerify`
    - 功能：订单退款审核
  - `POST` `/order/cancel` -> `cancelOrder`
    - 功能：订单取消
  - `POST` `/order/express` -> `express`
    - 功能：获取物流信息
  - `GET` `/order/getSubscribeTemplate` -> `getSubscribeTemplate`
    - 功能：获取订阅消息模板ID
  - `GET` `/order/tab/first` -> `getOrderTabFirst`
    - 功能：订单中心tab
  - `GET` `/order/tab` -> `getOrderTab`
    - 功能：订单中心tab
  - `POST` `/order/submit` -> `submitOrder`
    - 功能：提交订单

### `PayController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/pay/rest/PayController.java`
- Java包：`com.mailvor.modules.pay.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/pay/channel` -> `aliPay`
    - 功能：app支付
  - `POST` `/pay/confirm` -> `payConfirm`
    - 功能：支付请求短信确认
  - `GET` `/pay/bind` -> `bindChannel`
    - 功能：绑定支付渠道
  - `GET` `/pay/config` -> `extractConfig`
    - 功能：用户支付配置
  - `POST` `/pay/bank/bind` -> `payBankBind`
    - 功能：银行卡绑定签约
  - `POST` `/pay/bank/bind/confirm` -> `payBankBindConfirm`
    - 功能：银行卡绑定签约短信确认
  - `GET` `/pay/bank/info` -> `payBankInfo`
    - 功能：查询银行卡信息
  - `GET` `/pay/bank/support` -> `payBankBindConfirm`
    - 功能：查询支持的银行卡列表

### `PayNotifyController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/pay/rest/PayNotifyController.java`
- Java包：`com.mailvor.modules.pay.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/pay/notify/syb` -> `notify`
    - 功能：充值回调
  - `POST` `/pay/notify/ada` -> `adaPayNotify`
    - 功能：汇付充值回调
  - `POST` `/pay/notify/ali` -> `aliPayNotify`
    - 功能：支付宝充值回调
  - `POST` `/pay/notify/wechat` -> `wechatNotify`
    - 功能：微信APP支付回调
  - `POST` `/pay/notify/yee` -> `yeePayNotify`
    - 功能：易宝充值回调
  - `POST` `/pay/notify/yse` -> `ysePayNotify`
    - 功能：银盛充值回调
  - `POST` `/ios/notify` -> `iosNotify`
    - 功能：ios回调

### `StoreCategoryController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/product/rest/StoreCategoryController.java`
- Java包：`com.mailvor.modules.product.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/category` -> `getMwStoreCategoryPageList`
    - 功能：商品分类列表

### `StoreProductController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/product/rest/StoreProductController.java`
- Java包：`com.mailvor.modules.product.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/groom/list/{type}` -> `moreGoodsList`
    - 功能：获取首页更多产品
  - `GET` `/products` -> `goodsList`
    - 功能：商品列表
  - `GET` `/product/hot` -> `productRecommend`
    - 功能：为你推荐
  - `GET` `/product/poster/{id}` -> `prodoctPoster`
    - 功能：商品详情海报
  - `POST` `/collect/add` -> `collectAdd`
    - 功能：添加收藏
  - `POST` `/collect/del` -> `collectDel`
    - 功能：取消收藏
  - `GET` `/collect` -> `collect`
    - 功能：获取商品是否收藏
  - `POST` `/collect/addFoot` -> `footAdd`
    - 功能：添加足迹
  - `DELETE` `/collect/delFoot` -> `collectDelFoot`
    - 功能：删除足跡
  - `GET` `/reply/config/{id}` -> `replyCount`
    - 功能：获取产品评论数据
  - `GET` `/tao/orders` -> `tbOrders`
    - 功能：商品列表
  - `GET` `/jd/orders` -> `jdOrders`
    - 功能：商品列表
  - `GET` `/pdd/orders` -> `pddOrders`
    - 功能：商品列表
  - `GET` `/vip/orders` -> `vipOrders`
    - 功能：商品列表
  - `GET` `/dy/orders` -> `dyOrders`
    - 功能：抖音订单列表
  - `GET` `/mt/orders` -> `mtOrders`
    - 功能：美团商品列表

### `ArticleController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/shop/rest/ArticleController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/article`
- 接口列表：
  - `GET` `/article/details/{id}` -> `getMwArticle`
    - 功能：文章详情
  - `GET` `/article/list` -> `getMwArticlePageList`
    - 功能：文章列表

### `IndexController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/shop/rest/IndexController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/getCanvas` -> `getCanvas`
    - 功能：读取画布数据
  - `GET` `/index` -> `index`
    - 功能：首页数据
  - `GET` `/search/keyword` -> `search`
    - 功能：热门搜索关键字获取
  - `POST` `/image_base64` -> `imageBase64`
    - 功能：获取图片base64
  - `GET` `/citys` -> `cityJson`
    - 功能：获取城市json
  - `GET` `/store_list` -> `storeList`
    - 功能：获取门店列表

### `LimitController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/test/LimitController.java`
- Java包：`com.mailvor.modules.test`
- 基础路径：`/test`
- 接口列表：
  - `GET` `/test/limit` -> `testLimit`
    - 功能：测试

### `UpdateController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/update/rest/UpdateController.java`
- Java包：`com.mailvor.modules.update.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/update/config` -> `updateConfig`
    - 功能：app更新配置

### `UserAddressController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserAddressController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/city_list` -> `getTest`
    - 功能：城市列表
  - `POST` `/address/edit` -> `addMwUserAddress`
    - 功能：添加或修改地址
  - `POST` `/address/default/set` -> `setDefault`
    - 功能：设置默认地址
  - `POST` `/address/del` -> `deleteMwUserAddress`
    - 功能：删除用户地址
  - `GET` `/address/list` -> `getMwUserAddressPageList`
    - 功能：用户地址列表
  - `GET` `/address/detail/{id}` -> `addressDetail`
    - 功能：地址详情
  - `GET` `/address/default` -> `addressDefault`
    - 功能：默认地址

### `UserBillController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserBillController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/commission` -> `commission`
    - 功能：推广数据
  - `GET` `/integral/list` -> `userInfo`
    - 功能：账单记录
  - `GET` `/spread/banner` -> `spreadBanner`
    - 功能：分销二维码海报生成
  - `POST` `/spread/people` -> `spreadPeople`
    - 功能：推广人统计
  - `GET` `/spread/commission/{type}` -> `spreadCommission`
    - 功能：推广佣金明细
  - `POST` `/spread/order` -> `spreadOrder`
    - 功能：推广订单
  - `GET` `/spread/hb` -> `spreadHb`
    - 功能：拆红包奖励金额

### `UserCardController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserCardController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/user`
- 接口列表：
  - `POST` `/user/card/upload` -> `upload`
    - 功能：上传用户身份信息
  - `GET` `/user/card` -> `userInfo`
    - 功能：获取用户信息
  - `GET` `/user/card/bin` -> `cardBin`
    - 功能：银行卡识别
  - `GET` `/user/card/list` -> `cardList`
    - 功能：银行卡列表
  - `POST` `/user/card/bind` -> `cardBind`
    - 功能：银行卡绑定

### `UserController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/push` -> `push`
    - 功能：测试推送
  - `GET` `/userinfo` -> `userInfo`
    - 功能：获取用户信息
  - `GET` `/user/wxprofile` -> `userWxProfile`
    - 功能：获取用户微信信息
  - `GET` `/vipinfo` -> `userVipInfo`
    - 功能：获取用户会员信息
  - `GET` `/loginmustcode` -> `loginMustCode`
    - 功能：登录强制邀请码
  - `GET` `/hasUnlockOrder` -> `hasUnlockOrder`
    - 功能：是否包含已解锁红包
  - `GET` `/commissionInfo` -> `commissionInfo`
    - 功能：获取佣金信息
  - `DELETE` `/user/delete` -> `userDelete`
    - 功能：删除用户信息
  - `GET` `/user/spread` -> `userSpread`
    - 功能：获取用户上级信息
  - `GET` `/menu/user` -> `userMenu`
    - 功能：获取个人中心菜单
  - `GET` `/order/data` -> `orderData`
    - 功能：订单统计数据
  - `GET` `/collect/user` -> `collectUser`
    - 功能：获取收藏产品,或足迹
  - `GET` `/user/balance` -> `userBalance`
    - 功能：用户资金统计
  - `POST` `/sign/user` -> `sign`
    - 功能：签到用户信息
  - `GET` `/sign/config` -> `signConfig`
    - 功能：签到配置
  - `GET` `/sign/list` -> `signList`
    - 功能：签到列表
  - `GET` `/sign/month` -> `signMonthList`
    - 功能：签到列表（年月）
  - `POST` `/sign/integral` -> `signIntegral`
    - 功能：开始签到
  - `POST` `/user/binding` -> `binding`
    - 功能：用户绑定淘宝渠道id
  - `GET` `/user/auth/query` -> `authQuery`
    - 功能：方法 `authQuery`（未标注ApiOperation）
  - `POST` `/user/edit` -> `edit`
    - 功能：用户修改信息；备注：用修改信息
  - `POST` `/user/history/save` -> `historySave`
    - 功能：保存搜索历史
  - `GET` `/user/history` -> `history`
    - 功能：获取搜索历史
  - `DELETE` `/user/history/clear` -> `historyClear`
    - 功能：清空搜索历史
  - `POST` `/user/bind/mobile` -> `loginVerify`
    - 功能：绑定手机号
  - `GET` `/user/redis/delete` -> `loginVerify`
    - 功能：方法 `loginVerify`（未标注ApiOperation）
  - `GET` `/user/share` -> `userShare`
    - 功能：获取用户分享图
  - `GET` `/user/bank/list` -> `getMwUserAddressPageList`
    - 功能：用户签约银行卡列表

### `UserExtractController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserExtractController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/extract/bank` -> `bank`
    - 功能：提现参数
  - `GET` `/extract/config` -> `extractConfig`
    - 功能：用户提现配置
  - `POST` `/extract/cash` -> `addMwUserExtract`
    - 功能：用户提现
  - `GET` `/extract/list` -> `getMwUserExtractList`
    - 功能：提现列表
  - `POST` `/extract/bank/bind` -> `extractBankBind`
    - 功能：提现绑卡
  - `POST` `/extract/bank/bind/confirm` -> `extractBankBindConfirm`
    - 功能：提现绑卡短信确认

### `UserFeeController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserFeeController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/userfee` -> `userFee`
    - 功能：获取用户预估收益
  - `GET` `/userfeedetail` -> `userFeeDetail`
    - 功能：获取用户详细预估收益
  - `GET` `/userfeee` -> `userFeeE`
    - 功能：方法 `userFeeE`（未标注ApiOperation）

### `UserFeedbackController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserFeedbackController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/user/feedback/add` -> `create`
    - 功能：新增用户反馈

### `UserLevelController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserLevelController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/user/level` -> `getUserLevel`
    - 功能：会员等级
  - `GET` `/user/level/grade` -> `getLevelInfo`
    - 功能：会员等级列表
  - `GET` `/user/level/task/{id}` -> `getTask`
    - 功能：获取等级任务

### `UserRechargeController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserRechargeController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `GET` `/recharge/index` -> `getWays`
    - 功能：充值方案
  - `POST` `/recharge/wechat` -> `add`
    - 功能：公众号充值/H5充值
  - `POST` `/recharge/app/wechat` -> `wechatPay`
    - 功能：app充值
  - `POST` `/recharge/app/alipay` -> `aliPay`
    - 功能：支付宝支付
  - `POST` `/recharge/app/ios` -> `iosPay`
    - 功能：IOS支付
  - `POST` `/recharge/coupon` -> `payCoupon`
    - 功能：获取当前会员支付优惠金额
  - `POST` `/recharge/result` -> `rechargeResult`
    - 功能：记录银行卡支付结果

### `UserVerificationController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/user/rest/UserVerificationController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/user/face` -> `faceVerification`
    - 功能：支付宝人脸认证
  - `POST` `/user/face/result` -> `faceVerificationResult`
    - 功能：支付宝人脸认证结果查询

### `WechatController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/wechat/rest/controller/WechatController.java`
- Java包：`com.mailvor.modules.wechat.rest.controller`
- 基础路径：`/`
- 接口列表：
  - `GET` `/share` -> `share`
    - 功能：微信分享配置
  - `GET` `/wechat/config` -> `jsConfig`
    - 功能：jssdk配置
  - `GET` `/wxapp/config` -> `wxAppConfig`
    - 功能：微信小程序接口能力配置
  - `GET` `/wechat/serve` -> `authGet`
    - 功能：微信验证消息
  - `POST` `/wechat/serve` -> `post`
    - 功能：微信获取消息
  - `GET` `/wechat/id` -> `getWechatId`
    - 功能：获取微信公众号id

### `WechatLiveController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/wechat/rest/controller/WechatLiveController.java`
- Java包：`com.mailvor.modules.wechat.rest.controller`
- 基础路径：`/`
- 接口列表：
  - `GET` `/mwWechatLive` -> `getmwWechatLives`
    - 功能：查询所有直播间
  - `GET` `/mwWechatLive/getLiveReplay/{id}` -> `getLiveReplay`
    - 功能：获取直播回放

### `WxMaUserController`
- 类路径：`mshop-app/src/main/java/com/mailvor/modules/wechat/rest/controller/WxMaUserController.java`
- Java包：`com.mailvor.modules.wechat.rest.controller`
- 基础路径：`/`
- 接口列表：
  - `POST` `/binding` -> `verify`
    - 功能：公众号绑定手机号
  - `POST` `/wxapp/binding` -> `phone`
    - 功能：小程序绑定手机号

### `StoreAfterSalesController`
- 类路径：`mshop-app/src/main/java/com/mailvor/sales/rest/StoreAfterSalesController.java`
- Java包：`com.mailvor.sales.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/applyForAfterSales` -> `applyForAfterSales`
    - 功能：申请售后
  - `GET` `/applyForAfterSales/{key}` -> `checkOrderDetails`
    - 功能：查询订单详情
  - `GET` `/storeAfterSales/list` -> `salesList`
    - 功能：售后列表
  - `GET` `/store/detail/{key}/{id}` -> `detail`
    - 功能：订单详情
  - `GET` `/store/detail/{key}` -> `detail`
    - 功能：订单详情
  - `GET` `/revoke/{key}/{id}` -> `revoke`
    - 功能：撤销申请
  - `GET` `/mwExpress` -> `getMwExpresss`
    - 功能：查询快递
  - `DELETE` `/deleteAfterSalesOrder` -> `deleteAfterSalesOrder`
    - 功能：删除售后订单

### `EleController`
- 类路径：`mshop-app/src/main/java/com/mailvor/waimai/rest/EleController.java`
- Java包：`com.mailvor.waimai.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/ele/activity/code` -> `activityCode`
    - 功能：会场转链
  - `GET` `/ele/activity/list` -> `getActivityList`
    - 功能：查询美团活动列表

### `MeituanController`
- 类路径：`mshop-app/src/main/java/com/mailvor/waimai/rest/MeituanController.java`
- Java包：`com.mailvor.waimai.rest`
- 基础路径：`/`
- 接口列表：
  - `POST` `/mt/goods` -> `cities`
    - 功能：获取商品
  - `POST` `/mt/order/cps` -> `orderCPS`
    - 功能：获取cps订单
  - `GET` `/mt/activity/code` -> `activityCode`
    - 功能：会场转链
  - `GET` `/mt/activity/list` -> `getActivityList`
    - 功能：查询美团活动列表

## 模块：`mshop-gen`

### `GenConfigController`
- 类路径：`mshop-gen/src/main/java/com/mailvor/modules/gen/rest/GenConfigController.java`
- Java包：`com.mailvor.modules.gen.rest`
- 基础路径：`/api/genConfig`
- 接口列表：
  - `GET` `/api/genConfig/{tableName}` -> `get`
    - 功能：查询
  - `PUT` `/api/genConfig` -> `emailConfig`
    - 功能：修改

### `GeneratorController`
- 类路径：`mshop-gen/src/main/java/com/mailvor/modules/gen/rest/GeneratorController.java`
- Java包：`com.mailvor.modules.gen.rest`
- 基础路径：`/api/generator`
- 接口列表：
  - `GET` `/api/generator/tables/all` -> `getTables`
    - 功能：查询数据库数据
  - `GET` `/api/generator/tables` -> `getTables`
    - 功能：查询数据库数据
  - `GET` `/api/generator/columns` -> `getTables`
    - 功能：查询字段数据
  - `PUT` `/api/generator` -> `save`
    - 功能：保存字段数据
  - `POST` `/api/generator/sync` -> `sync`
    - 功能：同步字段数据
  - `POST` `/api/generator/{tableName}/{type}` -> `generator`
    - 功能：生成代码

## 模块：`mshop-log`

### `LogController`
- 类路径：`mshop-log/src/main/java/com/mailvor/modules/logging/rest/LogController.java`
- Java包：`com.mailvor.modules.logging.rest`
- 基础路径：`/api/logs`
- 接口列表：
  - `GET` `/api/logs/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/logs/error/download` -> `errorDownload`
    - 功能：导出错误数据
  - `GET` `/api/logs` -> `getLogs`
    - 功能：日志查询
  - `GET` `/api/logs/mlogs` -> `getApiLogs`
    - 功能：日志查询
  - `GET` `/api/logs/user` -> `getUserLogs`
    - 功能：用户日志查询
  - `GET` `/api/logs/error` -> `getErrorLogs`
    - 功能：错误日志查询
  - `GET` `/api/logs/error/{id}` -> `getErrorLogs`
    - 功能：日志异常详情查询
  - `DELETE` `/api/logs/del/error` -> `delAllByError`
    - 功能：删除所有ERROR日志
  - `DELETE` `/api/logs/del/info` -> `delAllByInfo`
    - 功能：删除所有INFO日志

## 模块：`mshop-mp`

### `ErrorController`
- 类路径：`mshop-mp/src/main/java/com/mailvor/modules/mp/error/ErrorController.java`
- Java包：`com.mailvor.modules.mp.error`
- 基础路径：`/error`
- 接口列表：
  - `GET` `/error/404` -> `error404`
    - 功能：方法 `error404`（未标注ApiOperation）
  - `GET` `/error/500` -> `error500`
    - 功能：方法 `error500`（未标注ApiOperation）

## 模块：`mshop-shop`

### `StoreBargainController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreBargainController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreBargain` -> `getMwStoreBargains`
    - 功能：查询砍价
  - `PUT` `/api/mwStoreBargain` -> `update`
    - 功能：修改砍价
  - `DELETE` `/api/mwStoreBargain/{id}` -> `delete`
    - 功能：删除砍价

### `StoreCombinationController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreCombinationController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreCombination` -> `getMwStoreCombinations`
    - 功能：查询拼团
  - `POST` `/api/mwStoreCombination` -> `add`
    - 功能：新增拼团
  - `GET` `/api/mwStoreCombination/info/{id}` -> `info`
    - 功能：获取商品信息
  - `PUT` `/api/mwStoreCombination` -> `update`
    - 功能：新增/修改拼团
  - `POST` `/api/mwStoreCombination/onsale/{id}` -> `onSale`
    - 功能：开启关闭
  - `DELETE` `/api/mwStoreCombination/{id}` -> `delete`
    - 功能：删除拼团

### `StoreCouponController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreCouponController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreCoupon` -> `getMwStoreCoupons`
    - 功能：查询
  - `POST` `/api/mwStoreCoupon` -> `create`
    - 功能：新增
  - `PUT` `/api/mwStoreCoupon` -> `update`
    - 功能：修改
  - `DELETE` `/api/mwStoreCoupon/{id}` -> `delete`
    - 功能：删除

### `StoreCouponIssueController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreCouponIssueController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreCouponIssue` -> `getMwStoreCouponIssues`
    - 功能：查询已发布
  - `POST` `/api/mwStoreCouponIssue` -> `create`
    - 功能：发布
  - `PUT` `/api/mwStoreCouponIssue` -> `update`
    - 功能：修改状态
  - `DELETE` `/api/mwStoreCouponIssue/{id}` -> `delete`
    - 功能：删除

### `StoreCouponIssueUserController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreCouponIssueUserController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreCouponIssueUser` -> `getMwStoreCouponIssueUsers`
    - 功能：查询
  - `DELETE` `/api/mwStoreCouponIssueUser/{id}` -> `delete`
    - 功能：删除

### `StoreCouponUserController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreCouponUserController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreCouponUser` -> `getMwStoreCouponUsers`
    - 功能：查询

### `StorePinkController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StorePinkController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStorePink` -> `getMwStorePinks`
    - 功能：查询记录

### `StoreSeckillController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/StoreSeckillController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreSeckill` -> `getMwStoreSeckills`
    - 功能：列表
  - `PUT` `/api/mwStoreSeckill` -> `update`
    - 功能：发布
  - `DELETE` `/api/mwStoreSeckill/{id}` -> `delete`
    - 功能：删除
  - `POST` `/api/mwStoreSeckill` -> `add`
    - 功能：新增秒杀
  - `GET` `/api/mwStoreSecKill/info/{id}` -> `info`
    - 功能：获取商品信息

### `UserExtractController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/activity/rest/UserExtractController.java`
- Java包：`com.mailvor.modules.activity.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwUserExtract` -> `getMwUserExtracts`
    - 功能：查询提现列表
  - `PUT` `/api/mwUserExtract` -> `update`
    - 功能：修改审核
  - `PUT` `/api/mwUserExtracts` -> `extracts`
    - 功能：修改审核批量
  - `GET` `/api/extract/ban/list` -> `getInvalidExtractList`
    - 功能：查询禁止提现用户
  - `POST` `/api/extract/ban` -> `invalidExtract`
    - 功能：新增禁止提现用户
  - `DELETE` `/api/extract/ban/{uid}` -> `delInvalidExtract`
    - 功能：删除禁止提现用户
  - `POST` `/api/extract/config` -> `editConfig`
    - 功能：修改提现配置
  - `GET` `/api/extract/config` -> `getConfig`
    - 功能：获取提现配置

### `StoreCanvasController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/canvas/rest/StoreCanvasController.java`
- Java包：`com.mailvor.modules.canvas.rest`
- 基础路径：`/api/canvas`
- 接口列表：
  - `POST` `/api/canvas/saveCanvas` -> `create`
    - 功能：新增或修改画布
  - `POST` `/api/canvas/upload` -> `create`
    - 功能：上传文件
  - `GET` `/api/canvas/getCanvas` -> `getCanvas`
    - 功能：读取画布数据
  - `DELETE` `/api/canvas` -> `deleteAll`
    - 功能：删除画布

### `StoreCategoryController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/category/rest/StoreCategoryController.java`
- Java包：`com.mailvor.modules.category.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreCategory/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwStoreCategory` -> `getMwStoreCategorys`
    - 功能：查询商品分类
  - `POST` `/api/mwStoreCategory` -> `create`
    - 功能：新增商品分类
  - `PUT` `/api/mwStoreCategory` -> `update`
    - 功能：修改商品分类
  - `DELETE` `/api/mwStoreCategory/{id}` -> `delete`
    - 功能：删除商品分类

### `MwStoreCustomerController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/customer/rest/MwStoreCustomerController.java`
- Java包：`com.mailvor.modules.customer.rest`
- 基础路径：`/api/mwStoreCustomer`
- 接口列表：
  - `GET` `/api/mwStoreCustomer/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwStoreCustomer` -> `getMwStoreCustomers`
    - 功能：查询customer
  - `POST` `/api/mwStoreCustomer` -> `create`
    - 功能：新增customer
  - `PUT` `/api/mwStoreCustomer` -> `update`
    - 功能：修改customer
  - `DELETE` `/api/mwStoreCustomer` -> `deleteAll`
    - 功能：删除customer

### `QrCodeController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/customer/rest/QrCodeController.java`
- Java包：`com.mailvor.modules.customer.rest`
- 基础路径：`/api/wxmp`
- 接口列表：
  - `GET` `/api/wxmp/qrcode` -> `qrcode`
    - 功能：方法 `qrcode`（未标注ApiOperation）
  - `GET` `/api/wxmp/wechatCode` -> `wechatCode`
    - 功能：方法 `wechatCode`（未标注ApiOperation）
  - `GET` `/api/wxmp/userInfo` -> `userInfo`
    - 功能：方法 `userInfo`（未标注ApiOperation）
  - `GET` `/api/wxmp/getOpenId` -> `userInfo`
    - 功能：方法 `userInfo`（未标注ApiOperation）

### `StoreOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/order/rest/StoreOrderController.java`
- Java包：`com.mailvor.modules.order.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreOrder/orderCount` -> `orderCount`
    - 功能：根据商品分类统计订单占比
  - `GET` `/api/data/count` -> `getCount`
    - 功能：根据商品分类统计订单占比
  - `GET` `/api/data/income` -> `getIncomeData`
    - 功能：方法 `getIncomeData`（未标注ApiOperation）
  - `GET` `/api/data/chart` -> `getChart`
    - 功能：方法 `getChart`（未标注ApiOperation）
  - `GET` `/api/mwStoreOrder` -> `getMwStoreOrders`
    - 功能：查询订单
  - `GET` `/api/getStoreOrderDetail/{id}` -> `getMwStoreOrders`
    - 功能：根据订单id获取订单详情
  - `GET` `/api/getNowOrderStatus/{id}` -> `getNowOrderStatus`
    - 功能：查询订单当前状态流程
  - `PUT` `/api/mwStoreOrder` -> `update`
    - 功能：发货
  - `PUT` `/api/mwStoreOrder/updateDelivery` -> `updateDelivery`
    - 功能：修改快递单号
  - `PUT` `/api/mwStoreOrder/check` -> `check`
    - 功能：订单核销
  - `DELETE` `/api/mwStoreOrder/{id}` -> `delete`
    - 功能：删除
  - `POST` `/api/mwStoreOrder/edit` -> `editOrder`
    - 功能：修改订单绑定用户
  - `POST` `/api/mwStoreOrder/remark` -> `editOrderRemark`
    - 功能：修改订单备注
  - `POST` `/api/mwStoreOrder/express` -> `express`
    - 功能：获取物流信息
  - `GET` `/api/mwStoreOrder/download` -> `download`
    - 功能：导出数据

### `PayChannelController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/pay/rest/PayChannelController.java`
- Java包：`com.mailvor.modules.pay.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/payset` -> `getPayChannelList`
    - 功能：查询支付通道
  - `POST` `/api/payset` -> `create`
    - 功能：新增支付通道
  - `PUT` `/api/payset` -> `update`
    - 功能：修改支付通道
  - `DELETE` `/api/payset/{id}` -> `delete`
    - 功能：删除支付通道
  - `POST` `/api/pay/config` -> `setPayConfig`
    - 功能：设置APP支付开关
  - `GET` `/api/pay/config` -> `setPayConfig`
    - 功能：获取APP支付开关

### `PayCompanyController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/pay/rest/PayCompanyController.java`
- Java包：`com.mailvor.modules.pay.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/paycompany` -> `getPayCompanyList`
    - 功能：查询数据配置
  - `POST` `/api/paycompany` -> `create`
    - 功能：新增通道
  - `PUT` `/api/paycompany` -> `update`
    - 功能：修改数据配置
  - `DELETE` `/api/paycompany/{id}` -> `delete`
    - 功能：删除数据配置

### `StoreProductController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/product/rest/StoreProductController.java`
- Java包：`com.mailvor.modules.product.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreProduct` -> `getMwStoreProducts`
    - 功能：查询商品
  - `POST` `/api/mwStoreProduct/addOrSave` -> `create`
    - 功能：新增/修改商品
  - `DELETE` `/api/mwStoreProduct/{id}` -> `delete`
    - 功能：删除商品
  - `POST` `/api/mwStoreProduct/onsale/{id}` -> `onSale`
    - 功能：商品上架/下架
  - `POST` `/api/mwStoreProduct/isFormatAttrForActivity/{id}` -> `isFormatAttrForActivity`
    - 功能：生成属性（添加活动产品专用）
  - `POST` `/api/mwStoreProduct/isFormatAttr/{id}` -> `isFormatAttr`
    - 功能：生成属性
  - `GET` `/api/mwStoreProduct/info/{id}` -> `info`
    - 功能：获取商品信息

### `StoreProductReplyController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/product/rest/StoreProductReplyController.java`
- Java包：`com.mailvor.modules.product.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwStoreProductReply` -> `getMwStoreProductReplys`
    - 功能：查询
  - `PUT` `/api/mwStoreProductReply` -> `update`
    - 功能：修改
  - `DELETE` `/api/mwStoreProductReply/{id}` -> `delete`
    - 功能：删除

### `StoreProductRuleController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/product/rest/StoreProductRuleController.java`
- Java包：`com.mailvor.modules.product.rest`
- 基础路径：`/api/mwStoreProductRule`
- 接口列表：
  - `GET` `/api/mwStoreProductRule/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwStoreProductRule` -> `getMwStoreProductRules`
    - 功能：查询sku规则
  - `POST` `/api/mwStoreProductRule/save/{id}` -> `create`
    - 功能：新增/修改sku规则
  - `DELETE` `/api/mwStoreProductRule` -> `deleteAll`
    - 功能：删除sku规则

### `StoreAfterSalesController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/sales/StoreAfterSalesController.java`
- Java包：`com.mailvor.modules.sales`
- 基础路径：`/api/mwStoreAfterSales`
- 接口列表：
  - `POST` `/api/mwStoreAfterSales/salesCheck` -> `salesCheck`
    - 功能：审核
  - `POST` `/api/mwStoreAfterSales/makeMoney` -> `makeMoney`
    - 功能：打款
  - `GET` `/api/mwStoreAfterSales/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwStoreAfterSales/sales/List` -> `getMwStoreAfterSaless`
    - 功能：查询售后
  - `POST` `/api/mwStoreAfterSales` -> `create`
    - 功能：新增售后
  - `PUT` `/api/mwStoreAfterSales` -> `update`
    - 功能：修改售后
  - `DELETE` `/api/mwStoreAfterSales` -> `deleteAll`
    - 功能：删除售后

### `ExpendController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/ExpendController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwExpend` -> `getList`
    - 功能：查询额外支出
  - `POST` `/api/mwExpend` -> `create`
    - 功能：新增额外支出
  - `PUT` `/api/mwExpend` -> `update`
    - 功能：修改额外支出
  - `DELETE` `/api/mwExpend/{id}` -> `delete`
    - 功能：删除额外支出

### `ExpressController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/ExpressController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwExpress` -> `getMwExpresss`
    - 功能：查询快递
  - `POST` `/api/mwExpress` -> `create`
    - 功能：新增快递
  - `PUT` `/api/mwExpress` -> `update`
    - 功能：修改快递
  - `DELETE` `/api/mwExpress/{id}` -> `delete`
    - 功能：删除快递

### `MaterialController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/MaterialController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/api/material`
- 接口列表：
  - `GET` `/api/material/page` -> `getMwMaterials`
    - 功能：查询素材管理
  - `POST` `/api/material` -> `create`
    - 功能：新增素材管理
  - `PUT` `/api/material` -> `update`
    - 功能：修改素材管理
  - `DELETE` `/api/material/{id}` -> `deleteAll`
    - 功能：删除素材管理

### `MaterialGroupController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/MaterialGroupController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/api/materialgroup`
- 接口列表：
  - `GET` `/api/materialgroup/page` -> `getMwMaterialGroups`
    - 功能：查询素材分组
  - `GET` `/api/materialgroup/list` -> `getMwMaterialGroupsList`
    - 功能：查询所有素材分组
  - `POST` `/api/materialgroup` -> `create`
    - 功能：新增素材分组
  - `PUT` `/api/materialgroup` -> `update`
    - 功能：修改素材分组
  - `DELETE` `/api/materialgroup/{id}` -> `deleteAll`
    - 功能：删除素材分组

### `MwAppVersionController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/MwAppVersionController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/api/mwAppVersion`
- 接口列表：
  - `GET` `/api/mwAppVersion/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwAppVersion` -> `getMwAppVersions`
    - 功能：查询app版本控制
  - `POST` `/api/mwAppVersion` -> `create`
    - 功能：新增app版本控制
  - `PUT` `/api/mwAppVersion` -> `update`
    - 功能：修改app版本控制
  - `DELETE` `/api/mwAppVersion` -> `deleteAll`
    - 功能：删除app版本控制

### `MwStoreProductRelationController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/MwStoreProductRelationController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/api/mwStoreProductRelation`
- 接口列表：
  - `GET` `/api/mwStoreProductRelation/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwStoreProductRelation` -> `getMwStoreProductRelations`
    - 功能：查询ProductRelation
  - `POST` `/api/mwStoreProductRelation` -> `create`
    - 功能：新增ProductRelation
  - `PUT` `/api/mwStoreProductRelation` -> `update`
    - 功能：修改ProductRelation
  - `DELETE` `/api/mwStoreProductRelation` -> `deleteAll`
    - 功能：删除ProductRelation

### `SystemConfigController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/SystemConfigController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwSystemConfig` -> `getMwSystemConfigs`
    - 功能：查询
  - `POST` `/api/mwSystemConfig` -> `create`
    - 功能：新增或修改

### `SystemGroupDataController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/SystemGroupDataController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwSystemGroupData` -> `getMwSystemGroupDatas`
    - 功能：查询数据配置
  - `POST` `/api/mwSystemGroupData` -> `create`
    - 功能：新增数据配置
  - `PUT` `/api/mwSystemGroupData` -> `update`
    - 功能：修改数据配置
  - `DELETE` `/api/mwSystemGroupData/{id}` -> `delete`
    - 功能：删除数据配置

### `SystemStoreController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/SystemStoreController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/api/mwSystemStore`
- 接口列表：
  - `GET` `/api/mwSystemStore/all` -> `getAll`
    - 功能：所有门店
  - `GET` `/api/mwSystemStore/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwSystemStore` -> `getMwSystemStores`
    - 功能：查询门店
  - `POST` `/api/mwSystemStore/getL` -> `create`
    - 功能：获取经纬度
  - `POST` `/api/mwSystemStore` -> `create`
    - 功能：新增门店
  - `PUT` `/api/mwSystemStore` -> `update`
    - 功能：修改门店
  - `DELETE` `/api/mwSystemStore` -> `deleteAll`
    - 功能：删除门店

### `SystemStoreStaffController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/shop/rest/SystemStoreStaffController.java`
- Java包：`com.mailvor.modules.shop.rest`
- 基础路径：`/api/mwSystemStoreStaff`
- 接口列表：
  - `GET` `/api/mwSystemStoreStaff/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwSystemStoreStaff` -> `getMwSystemStoreStaffs`
    - 功能：查询门店店员
  - `POST` `/api/mwSystemStoreStaff` -> `create`
    - 功能：新增门店店员
  - `PUT` `/api/mwSystemStoreStaff` -> `update`
    - 功能：修改门店店员
  - `DELETE` `/api/mwSystemStoreStaff` -> `deleteAll`
    - 功能：删除门店店员

### `ShippingTemplatesController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/template/rest/ShippingTemplatesController.java`
- Java包：`com.mailvor.modules.template.rest`
- 基础路径：`/api/mwShippingTemplates`
- 接口列表：
  - `GET` `/api/mwShippingTemplates/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwShippingTemplates` -> `getMwShippingTemplatess`
    - 功能：查询运费模板
  - `POST` `/api/mwShippingTemplates/save/{id}` -> `create`
    - 功能：新增/保存运费模板
  - `DELETE` `/api/mwShippingTemplates` -> `deleteAll`
    - 功能：删除运费模板
  - `GET` `/api/mwShippingTemplates/citys` -> `cityList`
    - 功能：方法 `cityList`（未标注ApiOperation）

### `MailvorDyOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/tk/rest/MailvorDyOrderController.java`
- Java包：`com.mailvor.modules.tk.rest`
- 基础路径：`/api/mailvorDyOrder`
- 接口列表：
  - `GET` `/api/mailvorDyOrder/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mailvorDyOrder` -> `getMailvorDyOrders`
    - 功能：查询dyOrder
  - `POST` `/api/mailvorDyOrder` -> `create`
    - 功能：新增dyOrder
  - `PUT` `/api/mailvorDyOrder` -> `update`
    - 功能：修改dyOrder
  - `DELETE` `/api/mailvorDyOrder` -> `deleteAll`
    - 功能：删除dyOrder

### `MailvorJdOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/tk/rest/MailvorJdOrderController.java`
- Java包：`com.mailvor.modules.tk.rest`
- 基础路径：`/api/mailvorJdOrder`
- 接口列表：
  - `GET` `/api/mailvorJdOrder/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mailvorJdOrder` -> `getMailvorJdOrders`
    - 功能：查询jdOrder
  - `POST` `/api/mailvorJdOrder` -> `create`
    - 功能：新增jdOrder
  - `PUT` `/api/mailvorJdOrder` -> `update`
    - 功能：修改jdOrder
  - `DELETE` `/api/mailvorJdOrder` -> `deleteAll`
    - 功能：删除jdOrder

### `MailvorMtOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/tk/rest/MailvorMtOrderController.java`
- Java包：`com.mailvor.modules.tk.rest`
- 基础路径：`/api/mailvorMtOrder`
- 接口列表：
  - `GET` `/api/mailvorMtOrder/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mailvorMtOrder` -> `getMailvorTbOrders`
    - 功能：查询mtOrder
  - `POST` `/api/mailvorMtOrder` -> `create`
    - 功能：新增mtOrder
  - `PUT` `/api/mailvorMtOrder` -> `update`
    - 功能：修改mtOrder
  - `DELETE` `/api/mailvorMtOrder` -> `deleteAll`
    - 功能：删除mtOrder

### `MailvorPddOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/tk/rest/MailvorPddOrderController.java`
- Java包：`com.mailvor.modules.tk.rest`
- 基础路径：`/api/mailvorPddOrder`
- 接口列表：
  - `GET` `/api/mailvorPddOrder/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mailvorPddOrder` -> `getMailvorPddOrders`
    - 功能：查询pddOrder
  - `POST` `/api/mailvorPddOrder` -> `create`
    - 功能：新增pddOrder
  - `PUT` `/api/mailvorPddOrder` -> `update`
    - 功能：修改pddOrder
  - `DELETE` `/api/mailvorPddOrder` -> `deleteAll`
    - 功能：删除pddOrder

### `MailvorTbOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/tk/rest/MailvorTbOrderController.java`
- Java包：`com.mailvor.modules.tk.rest`
- 基础路径：`/api/mailvorTbOrder`
- 接口列表：
  - `GET` `/api/mailvorTbOrder/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mailvorTbOrder` -> `getMailvorTbOrders`
    - 功能：查询tbOrder
  - `POST` `/api/mailvorTbOrder` -> `create`
    - 功能：新增tbOrder
  - `PUT` `/api/mailvorTbOrder` -> `update`
    - 功能：修改tbOrder
  - `DELETE` `/api/mailvorTbOrder` -> `deleteAll`
    - 功能：删除tbOrder

### `MailvorVipOrderController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/tk/rest/MailvorVipOrderController.java`
- Java包：`com.mailvor.modules.tk.rest`
- 基础路径：`/api/mailvorVipOrder`
- 接口列表：
  - `GET` `/api/mailvorVipOrder/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mailvorVipOrder` -> `getMailvorVipOrders`
    - 功能：查询vipOrder
  - `POST` `/api/mailvorVipOrder` -> `create`
    - 功能：新增vipOrder
  - `PUT` `/api/mailvorVipOrder` -> `update`
    - 功能：修改vipOrder
  - `DELETE` `/api/mailvorVipOrder` -> `deleteAll`
    - 功能：删除vipOrder

### `MemberCardController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/user/rest/MemberCardController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwUser/card` -> `getMwUsers`
    - 功能：查询开店用户
  - `POST` `/api/mwUser/card/clearSignature/{id}` -> `onStatus`
    - 功能：删除用户签名
  - `DELETE` `/api/mwUser/card/{id}` -> `delete`
    - 功能：删除开店用户
  - `DELETE` `/api/mwUser/card/bank/{id}` -> `deleteCardBank`
    - 功能：删除开店用户银行卡
  - `DELETE` `/api/mwUser/card/phone/{id}` -> `deleteCardPhone`
    - 功能：删除开店用户手机号
  - `GET` `/api/mwUser/card/re` -> `re`
    - 功能：查询加盟用户

### `MemberController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/user/rest/MemberController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`api`
- 接口列表：
  - `POST` `/api/mwUser/spread` -> `getSpread`
    - 功能：查看下级
  - `GET` `/api/mwUser` -> `getMwUsers`
    - 功能：查询用户
  - `GET` `/api/mwUserExtra/{uid}` -> `getUserExtra`
    - 功能：查询用户额外信息
  - `PUT` `/api/mwUser` -> `update`
    - 功能：修改用户
  - `DELETE` `/api/mwUser/wechat/{uid}` -> `deleteWechat`
    - 功能：删除用户微信授权信息
  - `DELETE` `/api/mwUser/ali/{uid}` -> `deleteAli`
    - 功能：删除用户支付宝授权信息
  - `POST` `/api/mwUser/reset/refund/{uid}` -> `resetRefund`
    - 功能：重置退款次数
  - `DELETE` `/api/mwUser/{uid}` -> `delete`
    - 功能：删除用户
  - `POST` `/api/mwUser/onStatus/{id}` -> `onStatus`
    - 功能：用户禁用启用
  - `POST` `/api/mwUser/money` -> `updatePrice`
    - 功能：修改余额
  - `POST` `/api/mwUser/integral` -> `updateIntegral`
    - 功能：修改积分
  - `POST` `/api/mwUser/bank/bind/{uid}` -> `extractBankBind`
    - 功能：后台提现绑卡
  - `POST` `/api/mwUser/bank/bind/confirm/{uid}` -> `extractBankBindConfirm`
    - 功能：后台提现绑卡短信确认
  - `POST` `/api/mwUser/extract/{uid}` -> `extract`
    - 功能：后台提现
  - `GET` `/api/order/unlock/config` -> `getOrderUnlockConfig`
    - 功能：查询红包解锁配置
  - `POST` `/api/order/unlock/config` -> `setOrderUnlockConfig`
    - 功能：修改红包解锁配置
  - `GET` `/api/app/data/config` -> `getAppDataConfig`
    - 功能：查询APP基础信息配置
  - `POST` `/api/app/data/config` -> `setAppDataConfig`
    - 功能：修改APP基础信息配置
  - `GET` `/api/app/share/config` -> `getAppShareConfig`
    - 功能：查询APP分享图配置
  - `POST` `/api/app/share/config` -> `setAppShareConfig`
    - 功能：修改APP分享图配置
  - `GET` `/api/app/login/whitelist` -> `getAppLoginWhitelist`
    - 功能：查询APP登录白名单
  - `POST` `/api/app/login/whitelist` -> `setAppLoginWhitelist`
    - 功能：修改APP登录白名单

### `SystemUserLevelController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/user/rest/SystemUserLevelController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwSystemUserLevel` -> `getMwSystemUserLevels`
    - 功能：查询
  - `POST` `/api/mwSystemUserLevel` -> `create`
    - 功能：新增
  - `PUT` `/api/mwSystemUserLevel` -> `update`
    - 功能：修改
  - `DELETE` `/api/mwSystemUserLevel/{id}` -> `delete`
    - 功能：删除

### `SystemUserTaskController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/user/rest/SystemUserTaskController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwSystemUserTask` -> `getMwSystemUserTasks`
    - 功能：查询
  - `POST` `/api/mwSystemUserTask` -> `create`
    - 功能：新增
  - `PUT` `/api/mwSystemUserTask` -> `update`
    - 功能：修改
  - `DELETE` `/api/mwSystemUserTask/{id}` -> `delete`
    - 功能：删除

### `UserBillController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/user/rest/UserBillController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwUserBill` -> `getMwUserBills`
    - 功能：查询

### `UserRechargeController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/user/rest/UserRechargeController.java`
- Java包：`com.mailvor.modules.user.rest`
- 基础路径：`/api/mwUserRecharge`
- 接口列表：
  - `GET` `/api/mwUserRecharge/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwUserRecharge` -> `getMwUserRecharges`
    - 功能：查询充值管理
  - `DELETE` `/api/mwUserRecharge` -> `deleteAll`
    - 功能：删除充值管理

### `MwWechatLiveController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/wechat/rest/MwWechatLiveController.java`
- Java包：`com.mailvor.modules.wechat.rest`
- 基础路径：`/api/mwWechatLive`
- 接口列表：
  - `GET` `/api/mwWechatLive/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwWechatLive` -> `getMwWechatLives`
    - 功能：查询wxlive
  - `POST` `/api/mwWechatLive` -> `create`
    - 功能：新增wxlive
  - `POST` `/api/mwWechatLive/addGoods` -> `addGoods`
    - 功能：添加商品
  - `PUT` `/api/mwWechatLive` -> `update`
    - 功能：修改wxlive
  - `DELETE` `/api/mwWechatLive` -> `deleteAll`
    - 功能：删除wxlive
  - `GET` `/api/mwWechatLive/synchro` -> `synchroWxOlLive`
    - 功能：同步数据

### `MwWechatLiveGoodsController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/wechat/rest/MwWechatLiveGoodsController.java`
- Java包：`com.mailvor.modules.wechat.rest`
- 基础路径：`/api/mwWechatLiveGoods`
- 接口列表：
  - `GET` `/api/mwWechatLiveGoods/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwWechatLiveGoods` -> `getMwWechatLiveGoodss`
    - 功能：查询mwWechatLiveGoods
  - `POST` `/api/mwWechatLiveGoods` -> `create`
    - 功能：新增mwWechatLiveGoods
  - `PUT` `/api/mwWechatLiveGoods` -> `update`
    - 功能：修改mwWechatLiveGoods
  - `DELETE` `/api/mwWechatLiveGoods` -> `deleteAll`
    - 功能：删除mwWechatLiveGoods
  - `POST` `/api/mwWechatLiveGoods/synchro` -> `synchroWxOlLiveGoods`
    - 功能：同步数据

### `WechatArticleController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/wechat/rest/WechatArticleController.java`
- Java包：`com.mailvor.modules.wechat.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwArticle/info/{id}` -> `getInfo`
    - 功能：查询单条信息
  - `GET` `/api/mwArticle` -> `getMwArticles`
    - 功能：查询
  - `POST` `/api/mwArticle` -> `create`
    - 功能：新增
  - `PUT` `/api/mwArticle` -> `update`
    - 功能：修改
  - `DELETE` `/api/mwArticle/{id}` -> `delete`
    - 功能：删除
  - `GET` `/api/mwArticle/publish/{id}` -> `publish`
    - 功能：发布文章

### `WechatMenuController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/wechat/rest/WechatMenuController.java`
- Java包：`com.mailvor.modules.wechat.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwWechatMenu` -> `getMwWechatMenus`
    - 功能：查询菜单
  - `POST` `/api/mwWechatMenu` -> `create`
    - 功能：创建菜单

### `WechatReplyController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/wechat/rest/WechatReplyController.java`
- Java包：`com.mailvor.modules.wechat.rest`
- 基础路径：`api`
- 接口列表：
  - `GET` `/api/mwWechatReply` -> `getMwWechatReplys`
    - 功能：查询
  - `POST` `/api/mwWechatReply` -> `create`
    - 功能：新增自动回复

### `WechatTemplateController`
- 类路径：`mshop-shop/src/main/java/com/mailvor/modules/wechat/rest/WechatTemplateController.java`
- Java包：`com.mailvor.modules.wechat.rest`
- 基础路径：`/api/mwWechatTemplate`
- 接口列表：
  - `GET` `/api/mwWechatTemplate/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwWechatTemplate` -> `getmwWechatTemplates`
    - 功能：查询微信模板消息
  - `POST` `/api/mwWechatTemplate` -> `create`
    - 功能：新增微信模板消息
  - `PUT` `/api/mwWechatTemplate` -> `update`
    - 功能：修改微信模板消息
  - `DELETE` `/api/mwWechatTemplate/{id}` -> `deleteAll`
    - 功能：删除微信模板消息

## 模块：`mshop-store`

### `MwUserFeedbackController`
- 类路径：`mshop-store/src/main/java/com/mailvor/modules/feedback/rest/MwUserFeedbackController.java`
- Java包：`com.mailvor.modules.feedback.rest`
- 基础路径：`/api/mwUserFeedback`
- 接口列表：
  - `GET` `/api/mwUserFeedback/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/mwUserFeedback` -> `getMwUserFeedbacks`
    - 功能：查询userFeedback
  - `POST` `/api/mwUserFeedback` -> `create`
    - 功能：新增userFeedback
  - `PUT` `/api/mwUserFeedback` -> `update`
    - 功能：修改userFeedback
  - `DELETE` `/api/mwUserFeedback` -> `deleteAll`
    - 功能：删除userFeedback

## 模块：`mshop-tool`

### `AliOssController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/AliOssController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`/api/alioss`
- 接口列表：
  - `POST` `/api/alioss/upload` -> `upload`
    - 功能：上传文件

### `EmailController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/EmailController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`api/email`
- 接口列表：
  - `GET` `/api/email` -> `get`
    - 功能：方法 `get`（未标注ApiOperation）
  - `PUT` `/api/email` -> `emailConfig`
    - 功能：配置邮件
  - `POST` `/api/email` -> `send`
    - 功能：发送邮件

### `LocalStorageController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/LocalStorageController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`/api/localStorage`
- 接口列表：
  - `GET` `/api/localStorage/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/localStorage` -> `getLocalStorages`
    - 功能：查询文件
  - `POST` `/api/localStorage` -> `create`
    - 功能：新增文件
  - `PUT` `/api/localStorage` -> `update`
    - 功能：修改文件
  - `DELETE` `/api/localStorage` -> `deleteAll`
    - 功能：删除文件

### `PictureController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/PictureController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`/api/pictures`
- 接口列表：
  - `GET` `/api/pictures` -> `getRoles`
    - 功能：查询图片
  - `GET` `/api/pictures/download` -> `download`
    - 功能：导出数据
  - `POST` `/api/pictures` -> `upload`
    - 功能：上传图片
  - `POST` `/api/pictures/synchronize` -> `synchronize`
    - 功能：同步图床数据
  - `DELETE` `/api/pictures` -> `deleteAll`
    - 功能：多选删除图片

### `QiniuController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/QiniuController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`/api/qiNiuContent`
- 接口列表：
  - `GET` `/api/qiNiuContent/config` -> `get`
    - 功能：方法 `get`（未标注ApiOperation）
  - `PUT` `/api/qiNiuContent/config` -> `emailConfig`
    - 功能：配置七牛云存储
  - `GET` `/api/qiNiuContent/download` -> `download`
    - 功能：导出数据
  - `GET` `/api/qiNiuContent` -> `getRoles`
    - 功能：查询文件
  - `POST` `/api/qiNiuContent` -> `upload`
    - 功能：上传文件
  - `POST` `/api/qiNiuContent/synchronize` -> `synchronize`
    - 功能：同步七牛云数据
  - `GET` `/api/qiNiuContent/download/{id}` -> `download`
    - 功能：下载文件
  - `DELETE` `/api/qiNiuContent/{id}` -> `delete`
    - 功能：删除文件
  - `DELETE` `/api/qiNiuContent` -> `deleteAll`
    - 功能：删除多张图片

### `UploadController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/UploadController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`/api/upload`
- 接口列表：
  - `POST` `/api/upload` -> `create`
    - 功能：上传文件

### `VerificationCodeController`
- 类路径：`mshop-tool/src/main/java/com/mailvor/modules/tools/rest/VerificationCodeController.java`
- Java包：`com.mailvor.modules.tools.rest`
- 基础路径：`/api/code`
- 接口列表：
  - `POST` `/api/code/resetEmail` -> `resetEmail`
    - 功能：重置邮箱，发送验证码
  - `POST` `/api/code/email/resetPass` -> `resetPass`
    - 功能：重置密码，发送验证码
  - `GET` `/api/code/validated` -> `validated`
    - 功能：验证码验证
