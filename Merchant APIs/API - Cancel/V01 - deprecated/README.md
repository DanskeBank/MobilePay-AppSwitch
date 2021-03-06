# CancelV01 service #
The service cancels a previously reserved or requested payment. The service can only be called for payments with status = RES (reserved), or REQ (requested).

### Input ###
|Parameter|Type|Description|
|:--------|:---|:----------|
|MerchantId|Char(60)|_Mandatory_. The MerchantId is generated by Danske Bank and sent to the merchant.|
|OrderId|Char(50)|_Mandatory._ The OrderId, which has originally been sent to the MobilePay AppSwitch SDK (generated by merchant).|
|TransactionId|Char(20)|_Optional_. This is the transaction id of the transaction from the authorization to DIBS.|
|CustomerId|Char(60)|_Optional_. Identification of customer in MobilePay systems (currently the phone number). CustomerId is only relevant for payments with status REQ.|
|Test|Char(1)|_Optional_. Test flag: Y/N. Default is 'N'. If Test = 'Y': The service returns OK, but does not cancel any payments.|

### Output ###
|Parameter|Type|Description|
|:--------|:---|:----------|
|ReturnCode|Char(2)|See return code table below.|
|ReasonCode|Char(2)|See reason code table below.|
|OriginalTransactionId|Char(20)|TransactionId of the original reservation or request.|

### Return and reason codes ###
The tables below describe the values of the fields *ReturnCode* and *ReasonCode* which are stated in the response from the CancelV01 service.

#### Return codes ####
|Value|Text|Description|
|:----|:---|:----------|
|00|Ok|Service completed without errors.|
|04|Warning|Service completed with validation errors.|
|08|Error|Service completed with errors.|
|24|Severe error|Service completed with errors that must be examined by Danske Bank.|

#### Reason codes ####
- Reason codes 1-19 are related to input validation errors.
- Reason codes 20-39 are related to other errors regarding specific input parameters.
- Reason codes 40-97 are related for other types of errors.
- Reason code 98 is a deadlock or timeout.
- Reason code 99 is related to errors that must be examined by Danske Bank.

|Value|Text|Description|
|:----|:---|:----------|
|00|Ok|Completed without errors|
|01|Invalid OrderId|OrderId is not specified or has an invalid value.|
|02|Invalid MerchantId|MerchantId is not specified or has an invalid value.|
|03|Invalid CustomerId|CustomerId is not specified or has an invalid value.|
|04|Invalid Test flag |Test flag is not specified or has an invalid value.|
|09|Invalid Date|The date specified is invalid or the format is wrong. DateFrom must be less than or equal to DateTo.|
|20|Merchant not found|The specified MerchantId could not be confirmed as an active Danske Bank customer.|
|21|Order not found|The specified OrderId for an existing order could not be found in the MobilePay backend (within the given time interval if specified).|
|23|Customer not found|The specified CustomerId could not be found in the MobilePay backend.|
|44|Call fails for non-technical reasons|The call has failed for a reason which cannot be disclosed. Stating the specific reason may disclose customer sensitive information (e.g. reaching daily MobilePay limit or having credit card revoked).|
|45|Transaction already captured|It is not possible to cancel a reservation, that has already been captured.|
|46|Transaction already cancelled|It is not possible to cancel a reservation, that has already been cancelled.|
|47|Reservation not found|It is not possible to cancel the reservation.|
|99|Technical error|The call did not succeed due to a technical error in the backend. The technical error must be examined by Danske Bank.|