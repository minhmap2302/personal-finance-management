SOFTWARE REQUIREMENTS SPECIFICATION (SRS)
Personal Finance Management – Hệ thống quản lý thu chi cá nhân và gia đình
Version: 1.0 (bản dự thảo) | Ngày: 20/10/2026
Nhóm: 5 thành viên | Thời gian: 3 ngày
THÔNG TIN KIỂM SOÁT TÀI LIỆU
Thuộc tính	Nội dung
Tên tài liệu	Đặc tả yêu cầu phần mềm – Personal Finance Management
Mã tài liệu	PFM-SRS-001
Phiên bản	1.1 – Draft (bổ sung đủ 5 vai trò)
Ngày lập	20/10/2026
Phụ trách	Scrum Master (chưa điền họ tên)
Trạng thái	Chờ nhóm và giảng viên xác nhận các giả định
Kho mã nguồn	github.com/minhmap2302/personal-finance-management (theo thông tin nhóm cung cấp)
Bảng công việc	PFM / Sprint 3 Days (Trello)


Lịch sử phiên bản
Phiên bản	Ngày	Nội dung thay đổi	Người thực hiện
1.0	20/10/2026	Khởi tạo SRS phục vụ Sprint 3 ngày	Scrum Master / nhóm
1.1	20/10/2026	Làm rõ danh sách 5 thành viên, trách nhiệm Full-stack và bảng ký xác nhận	Scrum Master / nhóm


Các nội dung cần xác nhận trước khi khóa yêu cầu
- Framework Java: Spring Boot được đề xuất, chưa có xác nhận cuối cùng.
- Frontend: HTML đã được thống nhất; CSS và JavaScript là thành phần đề xuất để tương tác với REST API.
- Hệ quản trị cơ sở dữ liệu: MySQL được đề xuất; cần chốt với Backend/DevOps.
- Phạm vi “gia đình”: có chia sẻ dữ liệu giữa nhiều tài khoản hay chỉ quản lý tài chính cá nhân? Bản MVP hiện ưu tiên dữ liệu riêng theo tài khoản.
- Mục tiêu tiết kiệm là yêu cầu nêu trong đề tài; mức ưu tiên cụ thể cần xác nhận với giảng viên nếu thiếu thời gian.
- Ngày/giờ nộp chính thức, máy chủ triển khai, domain/HTTPS và tiêu chí chấm của giảng viên chưa được cung cấp.
Mục lục nội dung
-
  1. Giới thiệu
-
  2. Phạm vi và mức ưu tiên
-
  3. Tác nhân và luồng nghiệp vụ
-
  4. Yêu cầu chức năng
-
  5. Quy tắc nghiệp vụ
-
  6. Yêu cầu phi chức năng
-
  7. Dữ liệu và cấu trúc lưu trữ
-
  8. Giao diện và hợp đồng API dự kiến
-
  9. Kiểm thử và nghiệm thu
-
  10. Kế hoạch thực hiện 3 ngày
-
  11. Rủi ro, phụ thuộc và các vấn đề mở
-
  12. Phụ lục – truy vết yêu cầu đến Trello
1. GIỚI THIỆU
1.1. Mục đích
Tài liệu xác định phạm vi, chức năng, quy tắc nghiệp vụ, ràng buộc kỹ thuật và tiêu chí chấp nhận của hệ thống Personal Finance Management. SRS là cơ sở thống nhất yêu cầu giữa Scrum Master, Backend, Full-stack, Frontend, DevOps & QA; đồng thời dùng để kiểm thử, nghiệm thu và chuẩn bị demo.
1.2. Mô tả bài toán
Người dùng cần một nơi lưu và tra cứu khoản thu, khoản chi, nhóm chi tiêu, ngân sách từng tháng, mục tiêu tiết kiệm và tình hình tài chính thông qua bảng tổng quan, biểu đồ. Hệ thống cần có đăng nhập, phân tách dữ liệu giữa các tài khoản và khả năng chạy ổn định bằng Docker Compose.
1.3. Thuật ngữ
Thuật ngữ	Diễn giải
MVP	Sản phẩm khả dụng tối thiểu để trình diễn và nghiệm thu trong 3 ngày
SRS	Software Requirements Specification – tài liệu đặc tả yêu cầu
Transaction	Giao dịch thu/chi có số tiền, ngày, danh mục
Category	Danh mục phân loại giao dịch: INCOME hoặc EXPENSE
Budget	Hạn mức chi tiêu theo tháng, có thể gắn danh mục
Savings Goal	Mục tiêu tiết kiệm và mức độ hoàn thành
JWT	JSON Web Token – phương thức xác thực API (đề xuất)
P0/P1/P2	Bắt buộc để chạy / Nên có theo đề tài / Có thể giản lược khi gấp
PR	Pull Request – đề xuất hợp nhất thay đổi mã nguồn


1.4. Nguồn và ràng buộc
- Đề tài 10: Backend gồm giao dịch, ngân sách, danh mục, thống kê tháng, phân quyền; Frontend gồm Dashboard, biểu đồ, lọc, cảnh báo vượt ngân sách; DevOps gồm Docker Compose, CI/CD, cấu hình môi trường, backup, bảo mật.
- Nhóm có 5 người: 1 Scrum Master và 4 người phụ trách Backend, Full-stack, Frontend, DevOps/QA.
- Deadline: 3 ngày; áp dụng 1 Sprint duy nhất. Tất cả hạng mục P0 phải được ưu tiên trước phần mở rộng.
- Nhóm đã trao đổi: Frontend HTML, Backend Java. Những lựa chọn còn lại đều là đề xuất, không coi là đã được phê duyệt.
2. PHẠM VI VÀ MỨC ƯU TIÊN
2.1. Phạm vi MVP ngày 1–3
Nhóm chức năng	Mức	Kết quả tối thiểu
Tài khoản & xác thực	P0	Đăng ký/đăng nhập, bảo vệ API và dữ liệu theo user
Danh mục thu chi	P0	Danh sách và CRUD danh mục
Giao dịch thu chi	P0	Thêm, xem, sửa, xóa; số tiền và loại giao dịch hợp lệ
Thống kê theo tháng	P0	Tổng thu, tổng chi, số dư, nhóm chi tiêu
Dashboard & biểu đồ	P0	Hiển thị dữ liệu từ API, không chỉ dữ liệu giả
Docker Compose	P0	Chạy được ứng dụng cùng cơ sở dữ liệu
Lọc giao dịch	P1	Theo tháng, loại, danh mục
Ngân sách & cảnh báo	P1	Lưu ngân sách tháng, phát hiện chi vượt hạn mức
CI/CD cơ bản	P1	Workflow build/test tự động; deploy nếu hạ tầng sẵn sàng
Backup cơ sở dữ liệu	P1	Có lệnh và tệp backup, hướng dẫn khôi phục
Tiết kiệm	P2*	CRUD mục tiêu, phần trăm tiến độ nếu đủ thời gian
Chia sẻ nhóm gia đình	P2*	Chưa thuộc MVP mặc định; cần chốt nếu yêu cầu bắt buộc


- P2 là đề xuất xử lý rủi ro deadline; nếu giảng viên yêu cầu tiết kiệm hoặc chức năng gia đình để chấm điểm, chuyển chức năng đó lên P1 và giảm phần nâng cao khác.
2.2. Ngoài phạm vi phiên bản 3 ngày
- Kết nối ngân hàng, ví điện tử, đồng bộ giao dịch tự động hoặc thanh toán trực tuyến.
- Dự báo chi tiêu bằng AI, đa tiền tệ, phân tích tài chính nâng cao.
- Phân quyền gia đình nhiều cấp, cơ chế phê duyệt chi tiêu phức tạp.
- Ứng dụng Android/iOS độc lập, gửi email/SMS tự động và hệ thống thông báo thời gian thực.
2.3. Giả định triển khai
Hệ thống triển khai dạng web responsive, dùng REST/JSON, một cơ sở dữ liệu quan hệ và đơn vị tiền mặc định VND. Người dùng truy cập bằng trình duyệt hiện đại. Các giá trị kỹ thuật cụ thể cần được nhóm thống nhất trước khi triển khai.
3. TÁC NHÂN VÀ LUỒNG NGHIỆP VỤ
3.1. Tác nhân
Tác nhân	Quyền và mô tả
Khách chưa đăng nhập	Truy cập trang đăng ký, đăng nhập; không xem dữ liệu thu chi.
Người dùng	Quản lý giao dịch, danh mục, ngân sách, mục tiêu (nếu có), xem Dashboard của chính mình.
Quản trị hệ thống (tùy chọn)	Có vai trò ADMIN ở Backend nếu cần; không mặc định được đọc dữ liệu tài chính cá nhân.
Thành viên gia đình (mở rộng)	Trong tương lai có thể chia sẻ nhóm tài chính theo cơ chế phân quyền riêng.


3.2. Các ca sử dụng chính
UC	Tên ca sử dụng	Tác nhân	Kết quả
UC-01	Đăng ký / đăng nhập	Khách	Có tài khoản và phiên đăng nhập
UC-02	Quản lý danh mục	Người dùng	Thêm/sửa/xóa/xem danh mục
UC-03	Quản lý giao dịch	Người dùng	Thu/chi được ghi nhận đúng
UC-04	Lọc giao dịch	Người dùng	Danh sách đúng điều kiện
UC-05	Thiết lập ngân sách	Người dùng	Hạn mức tháng được lưu
UC-06	Xem cảnh báo ngân sách	Người dùng	Nhận biết đã vượt hạn mức
UC-07	Xem Dashboard	Người dùng	Thống kê và biểu đồ chính xác
UC-08	Quản lý mục tiêu tiết kiệm (P2)	Người dùng	Theo dõi tiến độ tiết kiệm


3.3. Luồng UC-03 – Thêm giao dịch
1. Người dùng đã đăng nhập mở trang Giao dịch và chọn “Thêm giao dịch”.
2. Hệ thống hiển thị biểu mẫu: loại (thu/chi), số tiền, danh mục, ngày giao dịch, ghi chú.
3. Người dùng nhập dữ liệu và gửi biểu mẫu.
4. Frontend kiểm tra trường bắt buộc; Backend xác thực tài khoản, kiểm tra dữ liệu và quyền dùng danh mục.
5. Backend lưu giao dịch và trả kết quả; giao diện cập nhật danh sách, tổng thu/chi và cảnh báo ngân sách nếu liên quan.
6. Nếu dữ liệu lỗi hoặc phiên hết hạn, hệ thống thông báo cụ thể và không ghi dữ liệu sai.
3.4. Luồng UC-07 – Xem Dashboard
Sau đăng nhập, người dùng chọn tháng/năm; hệ thống lấy tổng thu, tổng chi, số dư và dữ liệu chi theo danh mục; giao diện hiển thị các thẻ số liệu và biểu đồ. Trường hợp không có giao dịch, tất cả chỉ số bằng 0 và có trạng thái rỗng dễ hiểu.
4. YÊU CẦU CHỨC NĂNG
4.1. Xác thực và tài khoản
FR-AUTH-01 – Đăng ký
Độ ưu tiên: P0. Người dùng cung cấp họ tên, email và mật khẩu; email không trùng; hệ thống lưu mật khẩu dạng băm an toàn.

Tiêu chí chấp nhận: Email hợp lệ được đăng ký; email trùng hoặc dữ liệu không hợp lệ bị từ chối; không trả mật khẩu qua API.

Liên kết Trello: BE-02, FE-02.
FR-AUTH-02 – Đăng nhập và đăng xuất
Độ ưu tiên: P0. Đăng nhập bằng email/mật khẩu, nhận token hoặc phiên hợp lệ; đăng xuất xóa thông tin đăng nhập phía client.

Tiêu chí chấp nhận: Thông tin đúng truy cập được; sai trả lỗi phù hợp; API riêng tư từ chối yêu cầu không xác thực.

Liên kết Trello: BE-02, FE-02.
FR-AUTH-03 – Phân tách dữ liệu theo chủ sở hữu
Độ ưu tiên: P0. Mọi bản ghi tài chính thuộc một tài khoản và Backend kiểm tra chủ sở hữu trong mọi API.

Tiêu chí chấp nhận: Tài khoản A không đọc, cập nhật hoặc xóa giao dịch/ngân sách/danh mục của tài khoản B.

Liên kết Trello: BE-06, FS-02, FS-06.
FR-AUTH-04 – Vai trò người dùng
Độ ưu tiên: P1. Backend hỗ trợ vai trò USER và ADMIN ở mức xác thực/phân quyền; người dùng tự đăng ký nhận USER, không tự nhận ADMIN.

Tiêu chí chấp nhận: Request từ USER không thể thực hiện endpoint chỉ dành cho ADMIN nếu endpoint này được triển khai.

Liên kết Trello: BE-06.
4.2. Danh mục thu/chi
FR-CAT-01 – CRUD danh mục
Độ ưu tiên: P0. Người dùng tạo, xem, sửa và xóa danh mục với tên, loại INCOME/EXPENSE; danh mục thuộc user hoặc là danh mục mặc định chỉ đọc.

Tiêu chí chấp nhận: CRUD hoạt động; không tạo danh mục trùng tên cùng loại trong phạm vi một người dùng (khuyến nghị); xóa danh mục đang được tham chiếu phải bị chặn hoặc có quy tắc an toàn.

Liên kết Trello: BE-03, FE-05.
4.3. Giao dịch thu/chi
FR-TRX-01 – CRUD giao dịch
Độ ưu tiên: P0. Mỗi giao dịch gồm loại, số tiền dương, danh mục tương ứng, ngày, ghi chú tùy chọn, chủ sở hữu.

Tiêu chí chấp nhận: Tạo/xem/sửa/xóa thành công; thu/chi đúng loại; số tiền bằng 0 hoặc âm bị từ chối; danh mục không hợp lệ không được sử dụng.

Liên kết Trello: BE-04, FE-03.
FR-TRX-02 – Lọc và sắp xếp
Độ ưu tiên: P1. Lọc theo tháng hoặc khoảng ngày, loại, danh mục; mặc định mới nhất trước.

Tiêu chí chấp nhận: Danh sách trả đúng bộ lọc, không lẫn dữ liệu người khác; dữ liệu rỗng không gây lỗi.

Liên kết Trello: BE-05, FE-03.
4.4. Ngân sách và cảnh báo
FR-BUD-01 – Thiết lập ngân sách
Độ ưu tiên: P1. Người dùng đặt hạn mức số tiền cho một tháng, có thể theo danh mục; hỗ trợ xem, cập nhật và xóa.

Tiêu chí chấp nhận: Không tồn tại hai ngân sách trùng cùng user/tháng/danh mục (hoặc một quy tắc thay thế nhất quán); số tiền phải dương.

Liên kết Trello: FS-01, FS-02, FE-05.
FR-BUD-02 – Cảnh báo vượt hạn mức
Độ ưu tiên: P1. Hệ thống tính tổng giao dịch EXPENSE thuộc kỳ và danh mục tương ứng; trả trạng thái OVER_BUDGET nếu tổng chi > ngân sách.

Tiêu chí chấp nhận: Dưới hạn mức: không cảnh báo; bằng hạn mức: chưa vượt; lớn hơn hạn mức: hiển thị mức vượt chính xác.

Liên kết Trello: FS-04, FE-05.
4.5. Dashboard và báo cáo
FR-REP-01 – Thống kê tổng quan tháng
Độ ưu tiên: P0. Theo tháng/năm, API trả totalIncome, totalExpense, balance = income – expense của user.

Tiêu chí chấp nhận: Kết quả khớp dữ liệu giao dịch, không cộng chéo tài khoản; không có dữ liệu trả 0.

Liên kết Trello: FS-03, FE-04.
FR-REP-02 – Biểu đồ theo danh mục
Độ ưu tiên: P0. API trả tổng chi theo danh mục; Frontend hiển thị biểu đồ và số liệu rõ ràng.

Tiêu chí chấp nhận: Dữ liệu biểu đồ khớp tổng chi trong kỳ; không có dữ liệu thì hiển thị trạng thái rỗng.

Liên kết Trello: FS-03, FE-04.
4.6. Tiết kiệm và gia đình – phạm vi mở rộng
FR-SAV-01 – Theo dõi mục tiêu tiết kiệm
Độ ưu tiên: P2*. Tạo mục tiêu với tên, số tiền mục tiêu, số tiền hiện có, hạn hoàn thành tùy chọn; tính tiến độ.

Tiêu chí chấp nhận: Tiến độ tính đúng; mục tiêu và số tiền nhập phải hợp lệ; người dùng chỉ sửa mục tiêu của mình.

Liên kết Trello: FS-05.
FR-FAM-01 – Nhóm tài chính gia đình
Độ ưu tiên: P2*. Nếu giảng viên yêu cầu chia sẻ thực sự, cho phép tạo nhóm, mời thành viên, xác định chủ nhóm và quyền xem/thêm giao dịch chung; cần thiết kế riêng trước khi làm.

Tiêu chí chấp nhận: Tối thiểu phải phân biệt dữ liệu cá nhân và dữ liệu chung; người ngoài nhóm không xem được dữ liệu nhóm.

Liên kết Trello: Chưa có thẻ trong Sprint 3 ngày; cần quyết định phạm vi.
5. QUY TẮC NGHIỆP VỤ
Mã	Quy tắc
BR-01	Mọi khoản giao dịch được nhập với amount > 0 và type ∈ {INCOME, EXPENSE}.
BR-02	Danh mục phải có cùng type với giao dịch tham chiếu.
BR-03	Số dư hiển thị = tổng thu − tổng chi; có thể âm và không phải số dư ngân hàng thực tế.
BR-04	Kỳ báo cáo tính theo múi giờ Asia/Ho_Chi_Minh, từ 00:00 ngày đầu tháng đến trước 00:00 ngày đầu tháng sau.
BR-05	Cảnh báo “vượt ngân sách” khi actualExpense > budgetAmount; bằng hạn mức không tính là vượt.
BR-06	Không được sửa dữ liệu của user khác, kể cả khi đoán được ID đối tượng.
BR-07	Tất cả khoản tiền dùng VND; Backend dùng DECIMAL (không tính tiền bằng float).
BR-08	Ngày tạo/cập nhật được lưu để truy vết; báo cáo theo transactionDate, không theo createdAt.
BR-09	Nếu danh mục đã được dùng, hệ thống từ chối xóa hoặc thực hiện quy tắc chuyển danh mục được xác nhận; không làm mất giao dịch âm thầm.
BR-10	Ngân sách theo danh mục không được cộng trùng với ngân sách tổng khi tính mức chi thực tế.


6. YÊU CẦU PHI CHỨC NĂNG
Mã	Nhóm	Yêu cầu / phép kiểm tra
NFR-01	Bảo mật	Mật khẩu băm bằng thuật toán thích hợp (BCrypt/Argon2); không commit secret vào repo.
NFR-02	Kiểm soát truy cập	API quan trọng yêu cầu xác thực; truy vấn lọc theo ownerId; hạn chế CORS về origin hợp lệ khi deploy.
NFR-03	Tính đúng đắn	Các phép tính tiền dùng kiểu số thập phân; dữ liệu thống kê phải khớp giao dịch nguồn.
NFR-04	Khả dụng	Ứng dụng có thể khởi động từ README + Docker Compose trên môi trường nhóm đã chuẩn bị.
NFR-05	Trải nghiệm	Các màn hình chính dùng được ở desktop; form có thông báo lỗi và trạng thái loading.
NFR-06	Hiệu năng tham khảo	Với dữ liệu demo ~1.000 giao dịch/người, các API danh sách/thống kê thường phản hồi trong khoảng 3 giây trên môi trường trình diễn bình thường; chỉ số mục tiêu, không phải cam kết SLA.
NFR-07	Khả năng bảo trì	Tách frontend, backend, cấu hình; code có README, .env.example và quy tắc branch/PR.
NFR-08	Triển khai	Docker Compose chạy các dịch vụ cần thiết; volume bảo toàn dữ liệu cơ sở dữ liệu khi khởi động lại container.
NFR-09	CI/CD	GitHub Actions chạy build và kiểm thử hiện có khi push/PR; deploy tự động khi đủ hạ tầng.
NFR-10	Backup	Có hướng dẫn xuất/khôi phục bản sao lưu; không lưu bản backup chứa dữ liệu cá nhân thật ở repository Public.
NFR-11	Quyền riêng tư	Dùng dữ liệu giả lập khi demo; không in token/mật khẩu trong log; không công khai thông tin tài chính cá nhân.
NFR-12	Tương thích	Hỗ trợ trình duyệt desktop phổ biến bản hiện đại; kiểm thử ít nhất một kích thước màn hình di động.


7. DỮ LIỆU VÀ CẤU TRÚC LƯU TRỮ
7.1. Mô hình dữ liệu logic
Quan hệ đề xuất: User 1–N Category; User 1–N Transaction; Category 1–N Transaction; User 1–N Budget; Category 1–N Budget (nullable nếu ngân sách tổng); User 1–N SavingsGoal (mở rộng). Đây là mô hình logic để nhóm chuyển thành ERD sau khi chốt cơ sở dữ liệu.
7.2. Từ điển dữ liệu tối thiểu
Bảng	Trường tối thiểu	Ghi chú
users	id, full_name, email, password_hash, role, created_at	email duy nhất
categories	id, user_id, name, type, created_at	type là INCOME/EXPENSE; có thể có default
transactions	id, user_id, category_id, type, amount, transaction_date, note, created_at, updated_at	amount DECIMAL(18,2), > 0
budgets	id, user_id, category_id?, month, year, amount, created_at	unique kỳ + danh mục theo user
savings_goals (P2)	id, user_id, name, target_amount, current_amount, deadline?	current_amount do người dùng cập nhật trong MVP


7.3. Ràng buộc dữ liệu
- Các trường ID và khóa ngoại được đánh chỉ mục phù hợp; kiểm tra liên kết owner khi thao tác.
- Các trường amount dùng DECIMAL(18,2) hoặc kiểu tiền tương đương chính xác.
- Không xóa cascade giao dịch khi người dùng xóa danh mục nếu chưa có quy tắc nghiệp vụ rõ ràng.
- Sử dụng migration hoặc script init có thể tái tạo schema cho môi trường Docker.
- Dữ liệu demo phải là dữ liệu giả; không đưa email/mật khẩu thật hoặc bản backup dữ liệu thật vào GitHub Public.
8. GIAO DIỆN VÀ HỢP ĐỒNG API DỰ KIẾN
8.1. Các trang web
Trang	Thành phần tối thiểu	Dữ liệu / hành động
Đăng ký / Đăng nhập	Form email, mật khẩu; thông báo lỗi	API auth
Dashboard	Tổng thu, tổng chi, số dư, 2 biểu đồ, bộ chọn tháng	API summary, categories breakdown
Giao dịch	Danh sách, tìm/lọc, form thêm/sửa, xóa	API transactions
Danh mục	Danh sách, thêm/sửa/xóa	API categories
Ngân sách	Thiết lập hạn mức tháng, tiến độ đã chi, cảnh báo	API budgets và budget status
Tiết kiệm (P2)	Mục tiêu, số tiền hiện có, % hoàn thành	API savings goals


8.2. Danh sách REST API dự kiến
Method	Endpoint	Mô tả
POST	/api/auth/register	Đăng ký
POST	/api/auth/login	Đăng nhập
GET	/api/categories	Danh mục của tài khoản
POST/PUT/DELETE	/api/categories[/{id}]	CRUD danh mục
GET	/api/transactions?month=&year=&type=&categoryId=	Lấy/lọc giao dịch
POST/PUT/DELETE	/api/transactions[/{id}]	CRUD giao dịch
GET	/api/budgets?month=&year=	Ngân sách tháng
POST/PUT/DELETE	/api/budgets[/{id}]	CRUD ngân sách
GET	/api/budgets/status?month=&year=	Tiến độ/cảnh báo hạn mức
GET	/api/reports/summary?month=&year=	Tổng thu, chi, số dư
GET	/api/reports/by-category?month=&year=	Chi tiêu theo danh mục
GET/POST/PUT/DELETE	/api/savings-goals[/{id}]	Mục tiêu tiết kiệm (P2)


Các endpoint là BẢN NHÁP API Contract; Backend/Frontend cần chốt URL, kiểu dữ liệu, format response và mã HTTP trước khi code. Các biểu thức [/{id}] là ký hiệu rút gọn, không phải URL gọi trực tiếp.
8.3. Ví dụ response thống kê (minh họa)
{
  "month": 10,
  "year": 2026,
  "currency": "VND",
  "totalIncome": 12000000,
  "totalExpense": 4500000,
  "balance": 7500000
}
8.4. Quy ước lỗi API
HTTP	Ý nghĩa	Ví dụ
400	Dữ liệu không hợp lệ	amount <= 0, thiếu danh mục
401	Chưa xác thực / token không hợp lệ	Không gửi token
403	Không có quyền	Truy cập chức năng bị hạn chế
404	Không tìm thấy trong phạm vi user	Không tồn tại giao dịch cần sửa
409	Xung đột	Email trùng hoặc ngân sách đã tồn tại
500	Lỗi ngoài dự kiến	Lỗi server; không trả stack trace cho client


9. KIỂM THỬ VÀ NGHIỆM THU
9.1. Các kịch bản kiểm thử chấp nhận
ID	Thao tác / dữ liệu kiểm thử	Kết quả mong đợi
AT-01	Đăng ký email mới và mật khẩu hợp lệ	Tạo user, mật khẩu lưu ở dạng băm
AT-02	Đăng nhập sai mật khẩu	Bị từ chối, không có token hợp lệ
AT-03	A lấy transaction ID của B	Không đọc được dữ liệu B
AT-04	Thêm giao dịch thu 5.000.000 VND	Thêm thành công, hiển thị trong danh sách
AT-05	Thêm giao dịch chi với amount = -1	HTTP 400, không lưu giao dịch
AT-06	Sửa rồi xóa giao dịch thuộc user	Dữ liệu cập nhật và biến mất khỏi danh sách
AT-07	Lọc giao dịch loại EXPENSE trong tháng	Chỉ trả đúng loại và thời gian
AT-08	Tạo danh mục chi và chọn khi thêm giao dịch	Giao dịch tham chiếu đúng danh mục
AT-09	Tạo ngân sách 1.000.000; chi 1.000.000	Không báo vượt
AT-10	Với ngân sách trên, thêm chi 100.000	Báo vượt 100.000 (nếu cùng phạm vi kỳ/danh mục)
AT-11	Tháng có thu 5.000.000 và chi 1.100.000	Dashboard: thu 5.000.000, chi 1.100.000, dư 3.900.000
AT-12	Tháng không có giao dịch	Chỉ số 0, biểu đồ/empty state không lỗi
AT-13	Chạy docker compose up -d trên máy thử	Các service khởi động; ứng dụng truy cập được
AT-14	Push/PR code có cấu hình build đúng	Workflow CI chạy thành công
AT-15	Xuất backup DB và thử đọc/khôi phục trong môi trường thử	Dữ liệu phục hồi được theo hướng dẫn
AT-16	Truy cập bằng điện thoại	Form, menu và thao tác chính dùng được


9.2. Định nghĩa hoàn thành (Definition of Done)
- Code đã commit và push vào nhánh tính năng; tạo Pull Request khi bàn giao.
- Có kiểm thử liên quan (manual test hoặc automated test) và bằng chứng kết quả.
- API / giao diện đã tích hợp với thành phần phụ thuộc; không chỉ có mock data.
- Không lộ secret, không còn lỗi nghiêm trọng chặn luồng demo.
- Scrum Master review, nghiệm thu theo tiêu chí đã mô tả trước khi chuyển thẻ Trello sang Đã xong.
9.3. Tiêu chí nghiệm thu toàn dự án
Hệ thống đạt mức MVP khi người dùng có thể đăng nhập, tạo danh mục, nhập/sửa/xóa khoản thu chi, xem thống kê tháng và biểu đồ từ dữ liệu thực; các chức năng P1 quan trọng theo đề bài (ngân sách/cảnh báo) được trình diễn; ứng dụng chạy qua Docker Compose và có hướng dẫn triển khai. Những hạng mục P2 chưa làm phải được công bố trung thực trong báo cáo.
10. KẾ HOẠCH THỰC HIỆN 3 NGÀY
Ngày	Mục tiêu	Đầu ra & kiểm tra
D1 – Foundation	DB/schema, auth, danh mục, khung Frontend, API ngân sách, Docker sơ bộ	Repo chung, API cơ bản có thể test, Frontend chạy, Dockerfile/Compose bản đầu
D2 – Integration	CRUD giao dịch, lọc, thống kê, Dashboard, ngân sách/cảnh báo, CI/CD	Demo nội bộ end-to-end; FE–BE–DB kết nối; bản deploy thử
D3 – Stabilization	Sửa lỗi, kiểm thử, bảo mật, backup, triển khai bản cuối, README, demo	Chạy demo ổn định; tài liệu và link nộp; không lỗi chặn demo


10.1. Phân công theo vai trò
Vai trò	Công việc chính / mã thẻ
Thành viên 1 – Scrum Master (SM)	SM-01…SM-04: chốt phạm vi, phân công, API Contract, quản lý GitHub/Trello, review PR, nghiệm thu và demo.
Thành viên 2 – Backend (BE)	BE-01…BE-07: thiết kế schema/migration chung, Java API tài khoản, JWT, danh mục, giao dịch, phân quyền và sửa lỗi.
Thành viên 3 – Full-stack (FS)	FS-01…FS-06: thiết kế ngân sách/tiết kiệm cùng BE, API ngân sách, thống kê tháng, cảnh báo vượt hạn mức; hỗ trợ FE tích hợp biểu đồ và API.
Thành viên 4 – Frontend (FE)	FE-01…FE-06: HTML/CSS/JavaScript, đăng nhập, giao dịch, Dashboard/Chart, lọc và kết nối REST API.
Thành viên 5 – DevOps & QA (DO)	DO-01…DO-05: Docker Compose, cấu hình môi trường, CI/CD, triển khai, backup, smoke test và hỗ trợ kiểm thử tích hợp.


10.2. Mốc khóa phạm vi và quy trình Git
Chốt API Contract ngay đầu D1. Các thành viên làm song song trên feature/backend, feature/fullstack, feature/frontend, feature/devops; gửi PR vào main để Scrum Master kiểm tra. Cuối D2 khóa việc phát triển tính năng mới; D3 ưu tiên sửa lỗi và demo. Quyền merge phải tuân thủ quy định nhóm và cấu hình bảo vệ nhánh thực tế trên GitHub.
11. RỦI RO VÀ VẤN ĐỀ MỞ
ID	Rủi ro / vấn đề	Phương án
R-01	Chưa chốt Java framework và database	Chốt trước khi giao code; ưu tiên stack nhóm đã quen, hạn chế đổi giữa Sprint.
R-02	Thời gian chỉ 3 ngày	P0 trước; P2 chỉ làm sau khi demo lõi chạy ổn định.
R-03	Xung đột schema BE và Full-stack	BE chịu trách nhiệm migration chính; FS review và dùng API Contract.
R-04	Frontend chờ API	Dùng mock response đúng API Contract; thay bằng API thực khi có.
R-05	Docker/CI/CD khó chạy trên VPS	Kiểm tra Docker local từ D1; có phương án demo Compose local nếu hạ tầng không sẵn.
R-06	Repo Public vô tình lộ thông tin	Dùng .gitignore và .env.example; rà soát secret trước mọi PR.
R-07	“Gia đình” có thể được hiểu là chia sẻ dữ liệu	Xác nhận với giảng viên; nếu bắt buộc, thiết kế phiên bản chia sẻ tối giản và đổi ưu tiên.
R-08	Quá tải Frontend ngày 2	FS hỗ trợ tích hợp biểu đồ, giản lược thẩm mỹ, ưu tiên hành trình giao dịch.
R-09	Thiếu quyền merge/branch rule phù hợp	Kiểm tra ruleset trên repo Public; xác nhận việc quản lý nhánh bằng PR.


11.1. Danh sách quyết định cần chốt
- [ ] Chốt Backend dùng Spring Boot hay một lựa chọn Java khác.
- [ ] Chốt MySQL/PostgreSQL và phiên bản triển khai.
- [ ] Chốt phạm vi “gia đình” có bắt buộc dùng chung dữ liệu không.
- [ ] Chốt mục tiêu tiết kiệm P1 hay P2.
- [ ] Chốt địa chỉ môi trường demo, domain/HTTPS, lịch nộp.
- [ ] Nhập tên thành viên và bổ sung phân công chính thức.
12. PHỤ LỤC – TRUY VẾT YÊU CẦU ĐẾN TRELLO
Yêu cầu	Card phụ trách	Bài kiểm thử
FR-AUTH-01/02	BE-02, FE-02	AT-01, AT-02
FR-AUTH-03/04	BE-06	AT-03
FR-CAT-01	BE-03, FE-05	AT-08
FR-TRX-01	BE-04, FE-03	AT-04, AT-05, AT-06
FR-TRX-02	BE-05, FE-03	AT-07
FR-BUD-01	FS-01, FS-02, FE-05	AT-09
FR-BUD-02	FS-04, FE-05	AT-09, AT-10
FR-REP-01/02	FS-03, FE-04	AT-11, AT-12
FR-SAV-01	FS-05	Bổ sung nếu thực hiện
NFR-08/09/10	DO-01…DO-05	AT-13, AT-14, AT-15
NFR-05/12	FE-06	AT-16


PHÊ DUYỆT VÀ BÀN GIAO
Vai trò	Họ tên / chữ ký	Ngày
Scrum Master	................................................	................
Đại diện Backend	................................................	................
Đại diện Full-stack	................................................	................
Đại diện Frontend	................................................	................
Đại diện DevOps / QA	................................................	................


Ghi chú: Tài liệu là bản SRS khởi tạo phục vụ phát triển nhanh. Sau khi nhóm xác nhận các mục mở, cần cập nhật thành phiên bản 1.1 và thống nhất với tiêu chí chấm thực tế.
