1. Parameter

| Key                   | Description                                                                       |       Type |
|:----------------------|:----------------------------------------------------------------------------------|-----------:|
| tenantId / accountId  | id đối tác được cung cấp bởi wiinvent                                             |    integer |
| channelId             | Danh sách id của category của nội dung & cách nhau bằng dấu ","                   |     string |
| category              | Danh sach tiêu đề category của nội dung & cách nhau bằng dấu ","                  |     string |
| streamId              | Id nội dung                                                                       |     string |
| title                 | Tiêu đề của nội dung                                                              |     string |
| vastLoadTimeout       | Vast Load Timeout                                                                 |    integer |
| mediaLoadTimeout      | Media Load Timeout                                                                |    integer |
| bufferingVideoTimeout | Buffering Video Timeout                                                           |    integer |                                  
| partnerSkipOffset     | Skip Ad Duration                                                                  |    integer |                                  
| environment           | Môi trường sanbox hoặc production                                                 |   constant |
| thirdPartyToken       | JWT Token from partner                                                            |     string |
| alwaysCustomSkip      | Biến hiển thị nút skip mặc định hay custom                                        |    boolean |
| bitrate               | max bitrate cho quảng cáo tvc                                                     |     number |
| tranId                | Mã giao dịch tạo từ server đối tác - client liên hệ server để biết thêm thông tin |     string |
| keyword               | Từ khoá tìm kiếm của nội dung (nếu có)                                            |     string |
| age                   | Tuổi (Nếu có)                                                                     |     number |
| gender                | Giới tính (nếu có)                                                                |   constant |
| uid20                 | Unified id 2.0 (nếu có)                                                           |     string |
| domainUrl             | link resource cdn của định dạng banner welcome                                    |     string |
| logLevel              | level của log , môi trường PRODUCTION cần set về mức NONE                         |     string |
| adSize                | Loại banner theo kích thước                                                       |   constant |
| contentType           | Loại nội dung là film, video hoặc tv                                              |   constant |

2. Constant

| Key         | Description                                                                        |
|:------------|:-----------------------------------------------------------------------------------|
| env         | Environment.SANDBOX <br/> WI.Environment.PRODUCTION                                |
| contentType | ContentType.TV <br/>WI.ContentType.FILM <br/>WI.ContentType.VIDEO <br/>WI.ContentType.SHORT_VOD                  |
| gender      | Gender.MALE <br/>WI.Gender.FEMALE <br/>WI.Gender.OTHER <br/>WI.Gender.NONE         |
| logLevel    | LevelLog.NONE <br/> LevelLog.BODY                                                  |
| adSize      | BannerAdSize.BANNER <br/> BannerAdSize.LARGE_BANNER <br/> BannerAdSize.RECTANGLE   |

4. Ads Callback

| Type      | Value      | Description                                   |
|:----------|:-----------|:----------------------------------------------|
| EventType | REQUEST    | Fired when the ad requests                    |
| EventType | LOADED     | Fired when the ad loaded                      |
| EventType | START      | Fired when the ad starts playing              |
| EventType | IMPRESSION | Fired when the impression URL has been pinged |
| EventType | CLICK      | Fired when the ad is clicked                  |
| EventType | COMPLETE   | Fired when the ad completes playing           |
| EventType | SKIPPED    | Fired when the ad is skipped by the user      |
| EventType | ERROR      | Fired when the ad has an error                |
| EventType | DESTROY    | Fires when the ad destroyed                   |

