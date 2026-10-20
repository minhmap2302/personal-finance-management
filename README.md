# PERSONAL FINANCE MANAGEMENT
## SOFTWARE REQUIREMENTS SPECIFICATION (SRS)

**Đề tài 10: Hệ thống quản lý thu chi cá nhân và gia đình**

**Phiên bản:** 1.1  
**Thời gian thực hiện:** 3 ngày  
**Số lượng thành viên:** 5 người  
**Phương pháp quản lý:** Agile/Scrum  
**Trạng thái:** Đang phát triển

---

# 1. GIỚI THIỆU DỰ ÁN

## 1.1. Mục đích

Personal Finance Management là hệ thống quản lý tài chính cá nhân và gia đình, giúp người dùng quản lý các khoản thu nhập, chi tiêu, ngân sách và mục tiêu tiết kiệm.

Hệ thống cung cấp giao diện trực quan để theo dõi tình hình tài chính, phân tích thói quen chi tiêu và hỗ trợ người dùng đưa ra quyết định tài chính hợp lý.

## 1.2. Mục tiêu

- Quản lý giao dịch thu nhập và chi tiêu.
- Phân loại các khoản thu chi theo danh mục.
- Thiết lập ngân sách hằng tháng.
- Theo dõi tình hình sử dụng ngân sách.
- Cảnh báo khi chi tiêu vượt ngân sách.
- Thống kê thu chi theo tháng.
- Hiển thị báo cáo thông qua Dashboard và biểu đồ.
- Theo dõi mục tiêu tiết kiệm.
- Bảo vệ thông tin tài chính của người dùng.
- Triển khai hệ thống bằng Docker và CI/CD.

## 1.3. Phạm vi dự án

Hệ thống được phát triển dưới dạng ứng dụng Web với các thành phần:

- Frontend: Giao diện tương tác với người dùng.
- Backend: Xử lý nghiệp vụ và cung cấp REST API.
- Database: Lưu trữ dữ liệu hệ thống.
- DevOps: Đóng gói, triển khai và tự động hóa.

Dự án được thực hiện trong 3 ngày nên ưu tiên hoàn thành các chức năng cốt lõi trước.

---

# 2. THÔNG TIN NHÓM PHÁT TRIỂN

## 2.1. Cơ cấu nhân sự

Nhóm gồm 5 thành viên:

| STT | Vai trò | Trách nhiệm |
|---|---|---|
| 1 | Scrum Master | Quản lý dự án, Trello, GitHub, tiến độ và nghiệm thu |
| 2 | Backend Developer | Database, Authentication, API giao dịch và danh mục |
| 3 | Full-stack Developer | API ngân sách, thống kê, tiết kiệm và hỗ trợ tích hợp |
| 4 | Frontend Developer | HTML, CSS, JavaScript, Dashboard và giao diện |
| 5 | DevOps & QA | Docker, CI/CD, triển khai, backup và kiểm thử |

## 2.2. Scrum Master

**Nhiệm vụ:**

- Phân tích và thống nhất yêu cầu dự án.
- Lập kế hoạch Sprint.
- Quản lý bảng Trello.
- Phân chia nhiệm vụ cho các thành viên.
- Theo dõi tiến độ thực hiện.
- Quản lý GitHub Repository.
- Review Pull Request.
- Merge code vào nhánh `main`.
- Điều phối xử lý các vấn đề phát sinh.
- Kiểm thử nghiệm thu.
- Tổng hợp tài liệu và chuẩn bị demo.

## 2.3. Backend Developer

**Nhiệm vụ:**

- Thiết kế Database.
- Xây dựng API đăng ký, đăng nhập.
- Xây dựng hệ thống xác thực người dùng.
- Xây dựng API CRUD giao dịch thu chi.
- Xây dựng API quản lý danh mục.
- Xây dựng API lọc giao dịch.
- Validation dữ liệu.
- Phân quyền truy cập.
- Kiểm thử và sửa lỗi Backend.

**Các thẻ Trello:** BE-01 đến BE-07.

## 2.4. Full-stack Developer

**Nhiệm vụ:**

- Thiết kế dữ liệu ngân sách.
- Thiết kế dữ liệu mục tiêu tiết kiệm.
- Xây dựng API quản lý ngân sách.
- Xây dựng API thống kê thu chi.
- Xây dựng logic cảnh báo vượt ngân sách.
- Xây dựng API mục tiêu tiết kiệm.
- Hỗ trợ Frontend tích hợp dữ liệu.
- Kiểm tra và sửa lỗi tích hợp.

**Các thẻ Trello:** FS-01 đến FS-06.

## 2.5. Frontend Developer

**Nhiệm vụ:**

- Thiết kế giao diện Web.
- Xây dựng giao diện đăng nhập, đăng ký.
- Xây dựng Dashboard.
- Xây dựng biểu đồ tài chính.
- Xây dựng giao diện quản lý giao dịch.
- Xây dựng giao diện quản lý danh mục.
- Xây dựng giao diện ngân sách.
- Tích hợp Backend API.
- Kiểm thử và tối ưu Responsive.

**Các thẻ Trello:** FE-01 đến FE-06.

## 2.6. DevOps & QA

**Nhiệm vụ:**

- Tạo Dockerfile.
- Cấu hình Docker Compose.
- Cấu hình biến môi trường.
- Thiết lập GitHub Actions.
- Tự động hóa build, test và deploy.
- Triển khai ứng dụng.
- Sao lưu Database.
- Kiểm tra bảo mật cơ bản.
- Kiểm thử hệ thống.
- Hỗ trợ xử lý lỗi triển khai.

**Các thẻ Trello:** DO-01 đến DO-05.

---

# 3. CÔNG NGHỆ SỬ DỤNG

| Thành phần | Công nghệ |
|---|---|
| Frontend | HTML5, CSS3, JavaScript |
| Backend | Java |
| Backend Framework | Spring Boot (đề xuất, chờ xác nhận) |
| Database | MySQL (đề xuất, chờ xác nhận) |
| API | REST API |
| Authentication | JWT (đề xuất) |
| Containerization | Docker |
| Orchestration | Docker Compose |
| CI/CD | GitHub Actions |
| Version Control | Git, GitHub |
| Project Management | Trello |
| Web Server | Nginx (nếu cần khi triển khai) |

---

# 4. YÊU CẦU CHỨC NĂNG

## FR-01. Quản lý tài khoản

Hệ thống phải hỗ trợ:

- Đăng ký tài khoản.
- Đăng nhập tài khoản.
- Đăng xuất.
- Xác thực người dùng.
- Bảo vệ các chức năng yêu cầu đăng nhập.

**Đầu vào:** Email, tên người dùng, mật khẩu.

**Đầu ra:** Trạng thái đăng nhập và thông tin xác thực.

**Tiêu chí nghiệm thu:**

- Người dùng đăng ký được tài khoản.
- Người dùng đăng nhập với thông tin hợp lệ.
- Mật khẩu được lưu dưới dạng mã hóa một chiều an toàn.
- Người chưa đăng nhập không truy cập được API được bảo vệ.

## FR-02. Quản lý giao dịch thu chi

Hệ thống phải cho phép:

- Thêm giao dịch.
- Xem danh sách giao dịch.
- Chỉnh sửa giao dịch.
- Xóa giao dịch.
- Phân biệt giao dịch thu nhập và chi tiêu.

Thông tin giao dịch gồm:

- Mã giao dịch.
- Loại giao dịch.
- Số tiền.
- Danh mục.
- Ngày thực hiện.
- Mô tả.
- Người sở hữu.

**Tiêu chí nghiệm thu:**

Người dùng có thể thực hiện đầy đủ CRUD giao dịch và không được phép sửa giao dịch của tài khoản khác.

## FR-03. Quản lý danh mục

Hệ thống hỗ trợ:

- Thêm danh mục.
- Xem danh sách danh mục.
- Chỉnh sửa danh mục.
- Xóa danh mục khi không vi phạm ràng buộc dữ liệu.
- Phân loại danh mục thu nhập và chi tiêu.

Ví dụ:

**Danh mục thu nhập:**

- Lương.
- Thưởng.
- Kinh doanh.
- Thu nhập khác.

**Danh mục chi tiêu:**

- Ăn uống.
- Mua sắm.
- Đi lại.
- Nhà ở.
- Giáo dục.
- Y tế.
- Giải trí.

## FR-04. Quản lý ngân sách

Người dùng có thể:

- Thiết lập ngân sách theo tháng.
- Thiết lập ngân sách theo danh mục.
- Xem ngân sách đã thiết lập.
- Cập nhật ngân sách.
- Xóa ngân sách.
- Theo dõi số tiền đã sử dụng.

**Quy tắc:**

Ngân sách còn lại = Ngân sách thiết lập - Tổng chi tiêu tương ứng.

## FR-05. Cảnh báo vượt ngân sách

Hệ thống phải:

- Tính tổng chi tiêu theo ngân sách.
- So sánh số tiền đã chi với hạn mức.
- Phát hiện trường hợp vượt ngân sách.
- Hiển thị cảnh báo trên giao diện.

**Điều kiện:**

Nếu tổng chi tiêu lớn hơn ngân sách, hệ thống hiển thị cảnh báo vượt ngân sách.

## FR-06. Thống kê thu chi

Hệ thống phải hỗ trợ:

- Thống kê thu nhập theo tháng.
- Thống kê chi tiêu theo tháng.
- Tính tổng số dư.
- Thống kê chi tiêu theo danh mục.
- Cung cấp dữ liệu cho biểu đồ.

**Công thức:**

Số dư = Tổng thu nhập - Tổng chi tiêu.

## FR-07. Dashboard

Dashboard hiển thị:

- Tổng thu nhập.
- Tổng chi tiêu.
- Số dư.
- Biểu đồ thu nhập và chi tiêu.
- Biểu đồ chi tiêu theo danh mục.
- Trạng thái ngân sách.
- Danh sách giao dịch gần đây.

Dữ liệu Dashboard phải được lấy từ API Backend.

## FR-08. Lọc giao dịch

Người dùng có thể lọc giao dịch theo:

- Ngày bắt đầu và kết thúc.
- Tháng và năm.
- Loại giao dịch.
- Danh mục.

Hệ thống phải hỗ trợ kết hợp các điều kiện lọc.

## FR-09. Mục tiêu tiết kiệm

Người dùng có thể:

- Tạo mục tiêu tiết kiệm.
- Đặt số tiền mục tiêu.
- Cập nhật số tiền đã tiết kiệm.
- Theo dõi tiến độ hoàn thành.
- Chỉnh sửa hoặc xóa mục tiêu.

**Công thức:**

Tiến độ (%) = Số tiền đã tiết kiệm / Số tiền mục tiêu × 100.

## FR-10. Phân quyền và bảo mật dữ liệu

Hệ thống phải đảm bảo:

- Người dùng chỉ truy cập dữ liệu được cấp quyền.
- API yêu cầu xác thực khi cần thiết.
- Không để lộ mật khẩu người dùng.
- Không công khai thông tin nhạy cảm.
- Kiểm tra quyền truy cập tại Backend.

Phạm vi chia sẻ tài chính giữa nhiều tài khoản trong một gia đình cần được nhóm xác nhận trước khi triển khai.

---

# 5. YÊU CẦU PHI CHỨC NĂNG

## NFR-01. Hiệu năng

- Các thao tác thông thường cần có thời gian phản hồi hợp lý.
- Hệ thống không bị treo khi thực hiện CRUD.
- Dashboard phải hiển thị dữ liệu chính xác.

## NFR-02. Bảo mật

- Mật khẩu phải được băm an toàn.
- Không lưu API Key hoặc mật khẩu trong source code.
- Không commit file `.env` chứa thông tin nhạy cảm.
- Kiểm tra dữ liệu đầu vào.
- Áp dụng xác thực và phân quyền tại Backend.

## NFR-03. Khả năng sử dụng

- Giao diện đơn giản, trực quan.
- Có thông báo khi thao tác thành công hoặc thất bại.
- Dễ dàng điều hướng giữa các trang.
- Hỗ trợ trình duyệt Web phổ biến.

## NFR-04. Khả năng triển khai

- Ứng dụng có Dockerfile.
- Có file Docker Compose.
- Có hướng dẫn cấu hình biến môi trường.
- Các thành phần có thể khởi động theo hướng dẫn README.

## NFR-05. Khả năng bảo trì

- Code được phân chia theo module.
- Có quy tắc đặt tên rõ ràng.
- Sử dụng Git để quản lý phiên bản.
- Các thay đổi phải có commit và Pull Request phù hợp.

## NFR-06. Sao lưu dữ liệu

- Có quy trình backup Database.
- Có hướng dẫn khôi phục dữ liệu.
- Hạn chế nguy cơ mất dữ liệu khi triển khai lại.

---

# 6. THIẾT KẾ DATABASE DỰ KIẾN

## 6.1. Users

| Trường | Kiểu dữ liệu dự kiến |
|---|---|
| id | BIGINT |
| username | VARCHAR |
| email | VARCHAR |
| password_hash | VARCHAR |
| created_at | DATETIME |

## 6.2. Categories

| Trường | Kiểu dữ liệu dự kiến |
|---|---|
| id | BIGINT |
| user_id | BIGINT |
| name | VARCHAR |
| type | VARCHAR |
| created_at | DATETIME |

## 6.3. Transactions

| Trường | Kiểu dữ liệu dự kiến |
|---|---|
| id | BIGINT |
| user_id | BIGINT |
| category_id | BIGINT |
| type | VARCHAR |
| amount | DECIMAL |
| description | TEXT |
| transaction_date | DATE |
| created_at | DATETIME |

## 6.4. Budgets

| Trường | Kiểu dữ liệu dự kiến |
|---|---|
| id | BIGINT |
| user_id | BIGINT |
| category_id | BIGINT |
| amount | DECIMAL |
| month | INT |
| year | INT |

## 6.5. SavingsGoals

| Trường | Kiểu dữ liệu dự kiến |
|---|---|
| id | BIGINT |
| user_id | BIGINT |
| name | VARCHAR |
| target_amount | DECIMAL |
| current_amount | DECIMAL |
| deadline | DATE |

**Lưu ý:** Đây là thiết kế logic ban
