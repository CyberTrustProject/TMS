# TMS REST API

**Version 1.4: 05/May/2020**

**Changelog**

| Date        | Description |
| ----------- | ----------- |
| 05/May/2020 | Document creation |

## List of Endpoints

**The REST API of the TMS is available at:**\
`http://172.16.4.11:8080/`


**Status field explanation:**\
:heavy_check_mark: = Completed\
:heavy_plus_sign: = Minor alterations required\
:heavy_minus_sign: = In Development\
:x: = Not Implemented

## Device trust
| #  | Request Method | Endpoint | Description | Status |
| :-: | -------------: | -------- | ----------- | :----: |
| 1  | GET  | /trust/info/{deviceId} | Returns the trust level for a device. The client may designate the desired trust dimensions. The information whether the reported trust level is explicit or implicit, is always returned. | :heavy_plus_sign: |
| 2  | PUT  | /trust/explicitLevel/{deviceId} | Explicitly specify the trust level for a device | :heavy_plus_sign: |
| 3  | DELETE  | /trust/explicitLevel/{deviceId} | Delete the explicitly specified trust level of the device, returning to automatic computation. | :heavy_plus_sign: |
| 4  | GET  | /trust | Returns trust level for a set of devices. The client may designate the desired trust dimensions. The information whether the reported trust level is explicit or implicit, is always returned. | :heavy_plus_sign: |

## PeerTMS
| #  | Request Method | Endpoint | Description | Status |
| :-: | -------------: | -------- | ----------- | :----: |
| 1  | GET  | /peerTMS/{peerTMSId} | Returns information for a peer TMS | :heavy_plus_sign: |
| 2  | DELETE  | /peerTMS/{peerTMSId} | Deletes/unregisters a peer TMS | :heavy_plus_sign: |
| 3  | PUT  | /peerTMS/{peerTMSId} | Creates or modifies a peer TMS | :heavy_plus_sign: |
| 4  | GET  | /peerTMS | Returns information for a designated set of TMS | :heavy_plus_sign: |
| 5  | GET  | /peerTMS/list/all | Returns information for all registered TMS | :heavy_minus_sign: |

## Risk Management
| #  | Request Method | Endpoint | Description | Status |
| :-: | -------------: | -------- | ----------- | :----: |
| 1  | GET  | /risks/prioritize | Returns the top risks, prioritized. The number of risks to return is described in the (optional) numRisks parameter. If missing, a default number is inserted | :x: |

## Trusted User
| #  | Request Method | Endpoint | Description | Status |
| :-: | -------------: | -------- | ----------- | :----: |
| 1  | GET  | /trustedUser/{trustedUserId} | Returns information for a trusted user | :heavy_plus_sign: |
| 2  | DELETE  | /trustedUser/{trustedUserId} | Deletes/unregisters a trusted user | :heavy_plus_sign: |
| 3  | PUT  | /trustedUser/{trustedUserId} | Creates or modifies a trusted user | :heavy_plus_sign: |
| 4  | GET  | /trustedUser | returns information for a designated set of trusted users | :heavy_plus_sign: |
| 5  | GET  | /trustedUser/list/all | Returns information for all trusted users | :heavy_plus_sign: |

## Trusted Entity
| #  | Request Method | Endpoint | Description | Status |
| :-: | -------------: | -------- | ----------- | :----: |
| 1  | POST  | /trustedEntity | Adds information about a new trusted entity, specifying the name and the public certificate of the entity. | :x: |
