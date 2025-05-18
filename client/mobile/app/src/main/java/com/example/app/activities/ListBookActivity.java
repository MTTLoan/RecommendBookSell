package com.example.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;
import com.example.app.models.Image;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ListBookActivity extends AppCompatActivity {
    private BookAdapter listBookAdapter;
    private RecyclerView listBookRecyclerView;
    private List<Book> bookList;
    private List<Book> filteredBookList; // List to hold filtered books
    private ImageView ivReturn;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listbook);

        // Initialize views
        listBookRecyclerView = findViewById(R.id.listBookRecyclerView);
        ivReturn = findViewById(R.id.ivReturn);
        tvTitle = findViewById(R.id.tvTitle);

        // Get category ID and name from Intent
        int categoryId = getIntent().getIntExtra("category_id", 0); // Default to 0 (all books)
        String categoryName = getIntent().getStringExtra("category_name");
        if (categoryName != null) {
            tvTitle.setText(categoryName); // Set the title to the category name
        } else {
            tvTitle.setText("Danh sách sách");
        }

        ivReturn.setOnClickListener(v -> finish());

        // Initialize book list
        filteredBookList = new ArrayList<>();

        // Sample book data (increased to test scrolling)
        bookList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//        // Book 1
//        List<Image> images1 = new ArrayList<>();
//        images1.add(new Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png",
//                "Cover for Cùng con trưởng thành"));
//        bookList.add(new Book(
//                1,
//                "Cùng con trưởng thành - Mình không thích bị cô lập",
//                "Lời nói đầu Bé mới tầm 1 tuổi đã cần đọc sách chưa? Bố mẹ muốn cho bé tiếp xúc sớm với sách được chứ? Bé còn nhỏ quá, sợ cầm sách thì xé mất! Bé mới ít tuổi thế đã nhận thức được gì chưa mà đọc sách? Đúng là trẻ nhỏ chưa cần “đọc” sách, mà các con cần được “chơi” với sách như là một món đồ chơi giàu tính tương tác. Sách cho lứa tuổi dưới 3 thường phải đảm bảo các tiêu chí: giấy dày để trẻ khó xé hay cắn hỏng, hình ảnh rất to rõ và thường ít màu để phù hợp với khả năng tiếp thu của trẻ, có hiệu ứng tương tác như lật giở-sờ chạm… để trẻ được vận động tay chân với sách, in bằng màu tốt và phủ bóng để đảm bảo an toàn nếu trẻ lỡ cắn hay liếm sách, nội dung thật gần gũi với nhu cầu và tâm lý lứa tuổi để bố mẹ dễ nói chuyện thủ thỉ với trẻ nhằm tăng cường giao tiếp thân mật…",
//                images1,
//                28000.0,
//                3.5,
//                2,
//                10,
//                2,
//                "2025-04-28 02:27:21",
//                "Unknown Author" // Author unclear from description
//        ));
//
//        // Book 2
//        List<Image> images2 = new ArrayList<>();
//        images2.add(new Image("https://salt.tikicdn.com/ts/product/5b/21/12/3d905ef72b7de07171761e4b1819543c.jpg",
//                "Cover for Rèn luyện Kỹ Năng Sống"));
//        bookList.add(new Book(
//                2,
//                "Rèn luyện Kỹ Năng Sống dành cho học sinh - 25 thói quen tốt để thành công",
//                "Rèn Luyện Kĩ Năng Sống Dành Cho Học Sinh - 25 Thói Quen Tốt Để Thành Công Giới thiệu tác phẩm - Hãy động não, thay đổi cách suy nghĩ, bạn có thể là một học sinh thiên tài! - Hãy để thói quen tốt chiến thắng thói quen xấu để trở thành một người thành công! Dành tặng tất cả những trẻ em mơ ước trở thành thiên tài Lười biếng, mất vệ sinh, làm việc chậm chạp lề mề… Các bạn nhỏ có phải có những thói quen khiến người khác bực mình? Đừng coi thường sự ảnh hưởng của thói quen xấu này nhé! Chúng giống như chiếc kẹo cao su, khi đã dính vào người sẽ rất khó dứt ra đấy. Bắt đầu từ bây giờ, hãy học cách coi thói quen tốt như bạn thân của mình và loại bỏ thói quen xấu đi, các bạn nhé!",
//                images2,
//                35000.0,
//                4.0,
//                5,
//                15,
//                2,
//                "2025-04-29 10:15:30",
//                "Unknown Author" // Author unclear from description
//        ));
//
//        // Book 3
//        List<Image> images3 = new ArrayList<>();
//        images3.add(new Image("https://salt.tikicdn.com/ts/product/16/72/77/dff96564663b63ba96b2c74b60261dcd.jpg",
//                "Cover for Xứ Sở Miên Man"));
//        bookList.add(new Book(
//                3,
//                "Xứ Sở Miên Man",
//                "Xứ Sở Miên Man Giới thiệu tác giả Jun Phạm, tên thật là Phạm Duy Thuận, không chỉ được biết đến là một ca sĩ, diễn viên tài năng mà còn là một nhà văn trẻ đầy triển vọng. Anh từng là thành viên của nhóm nhạc 365daband, một trong những nhóm nhạc nổi tiếng nhất Việt Nam. Tuy nhiên, bên cạnh đam mê ca hát, Jun Phạm còn có một tình yêu sâu sắc với văn chương. Jun Phạm bắt đầu con đường văn chương từ rất sớm. Anh thường xuyên chia sẻ những suy nghĩ, cảm xúc của mình trên trang cá nhân và nhận được sự ủng hộ nhiệt tình từ người hâm mộ. Chính điều này đã thôi thúc anh cho ra đời những cuốn sách của mình.",
//                images3,
//                45000.0,
//                4.5,
//                10,
//                20,
//                2,
//                "2025-04-30 14:20:45",
//                "Jun Phạm" // Corrected from Nhật Sơn
//        ));
//
//        // Book 4
//        List<Image> images4 = new ArrayList<>();
//        images4.add(new Image("https://salt.tikicdn.com/ts/product/56/bc/59/f63f4561ee47a86e1843e671fc6355e5.jpg",
//                "Cover for Tuổi Thơ Dữ Dội"));
//        bookList.add(new Book(
//                4,
//                "Tuổi Thơ Dữ Dội - Tập 2",
//                "Tuổi Thơ Dữ Dội là một câu chuyện hay, cảm động viết về tuổi thơ. Sách dày 404 trang mà người đọc không bao giờ muốn ngừng lại, bị lôi cuốn vì những nhân vật ngây thơ có, khôn ranh có, anh hùng có, vì những sự việc khi thì ly kỳ, khi thì hài hước, khi thì gây xúc động đến ứa nước mắt. Tuổi Thơ Dữ Dội không phải chỉ là một câu chuyện cổ tích, mà là một câu chuyện có thật ở trần gian, ở đó, những con người tuổi nhỏ đã tham gia vào cuộc kháng chiến chống xâm lược bảo vệ Tổ quốc với một chuỗi những chiến công đầy ắp li kì và hấp dẫn.",
//                images4,
//                52000.0,
//                4.2,
//                8,
//                12,
//                2,
//                "2025-05-01 09:30:00",
//                "Phùng Quán" // Corrected from Mai Anh
//        ));
//
//        // Book 5
//        List<Image> images5 = new ArrayList<>();
//        images5.add(new Image("https://salt.tikicdn.com/ts/product/0f/f9/70/e273b6980de4f6f550329aafe91578d8.jpg",
//                "Cover for Búp Sen Xanh"));
//        bookList.add(new Book(
//                5,
//                "Búp Sen Xanh",
//                "Câu chuyện khoa học về vòng tuần hoàn của nước. Búp Sen Xanh là tác phẩm nổi tiếng của nhà văn Sơn Nam, tái hiện cuộc đời của Chủ tịch Hồ Chí Minh thời niên thiếu, gắn liền với những giá trị văn hóa và lịch sử của dân tộc Việt Nam.",
//                images5,
//                30000.0,
//                3.8,
//                3,
//                18,
//                3,
//                "2025-05-02 16:45:10",
//                "Sơn Nam" // Corrected from Sơn Tùng
//        ));
//
//        // Book 6
//        List<Image> images6 = new ArrayList<>();
//        images6.add(new Image("https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg",
//                "Cover for Chuyện Con Mèo Dạy Hải Âu Bay"));
//        bookList.add(new Book(
//                6,
//                "Chuyện Con Mèo Dạy Hải Âu Bay",
//                "Sinh năm 1949 tại Chile, Luis Sepúlveda đã trải qua những năm tháng đầy biến động dưới chế độ độc tài của Augusto Pinochet. Chính những trải nghiệm sống thực tế đã trở thành nguồn cảm hứng lớn cho các tác phẩm của ông. Sepúlveda bắt đầu sự nghiệp viết lách từ năm 17 tuổi, nhưng phải đến những năm 1980, tên tuổi của ông mới thực sự được biết đến rộng rãi với tiểu thuyết Lão già mê đọc truyện tình. Cuốn sách này đã nhanh chóng trở thành một hiện tượng văn học, với hơn 18 triệu bản được bán ra trên toàn thế giới. Lời nói đầu/Giới thiệu sách Chuyện con mèo dạy hải âu bay là kiệt tác dành cho thiếu nhi của nhà văn Chi Lê nổi tiếng Luis Sepúlveda – tác giả của cuốn Lão già mê đọc truyện tình đã bán được 18 triệu bản khắp thế giới.",
//                images6,
//                28000.0,
//                3.5,
//                2,
//                10,
//                2,
//                "2025-04-28 02:27:21",
//                "Luis Sepúlveda" // Corrected from Nguyễn Nhật Ánh
//        ));
//
//        // Book 7
//        List<Image> images7 = new ArrayList<>();
//        images7.add(new Image("https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg",
//                "Cover for Những Con Mèo Sau Bức Tường Hoa"));
//        bookList.add(new Book(
//                7,
//                "Những Con Mèo Sau Bức Tường Hoa",
//                "Thông tin sản phẩm NHỮNG CON MÈO SAU BỨC TƯỜNG HOA Tác giả: Hà Mi; Minh họa: Trần Lê Nguyên Hà Thể loại: Truyện thiếu nhi; Số trang: 176 trang Khổ: 13x18 cm Giá: 69.000đ ISBN: 978-604-490-293-7 – Mã:8935069923897 Nhà xuất bản Phụ nữ Việt Nam phát hành trên toàn quốc tháng 9 năm 2024 Giới thiệu sách Những con mèo sau bức tường hoa, truyện thiếu nhi lọt vào vòng chung khảo giải thưởng Dế mèn, là câu chuyện về một gia đình nhỏ nằm sau bức tường hoa huỳnh vàng. Trong căn nhà ấy có đôi vợ chồng trung niên sống cùng sáu chú mèo: asymmetrical, Lulu, Fufu, Tom, Cục Mỡ, và Coco. Bạn sẽ nghĩ đôi vợ chồng ấy – ông bà Bắp Cải – hẳn rất yêu mèo mới có thể nuôi nhiều mèo đến vậy. Nhưng không, ban đầu hai ông bà vốn chẳng yêu mèo.",
//                images7,
//                35000.0,
//                4.0,
//                5,
//                15,
//                1,
//                "2025-04-29 10:15:30",
//                "Hà Mi"
//        ));
//
//        // Book 8
//        List<Image> images8 = new ArrayList<>();
//        images8.add(new Image("https://salt.tikicdn.com/ts/product/a7/24/37/42434f74d352fade0090a0d3790b0e9b.jpg",
//                "Cover for Bộ ba phép thuật"));
//        bookList.add(new Book(
//                8,
//                "Bộ ba phép thuật - Úm ba la ánh sáng hiện ra",
//                "Bộ sách tranh kể về chuyến phiêu lưu trong thế giới phép thuật của gấu Hoggs cùng hai người bạn chồn Poki và thỏ Fips. Bộ ba cưỡi thảm thần, học thần chú, gặp những nhân vật có phép thuật như phù thủy, nàng tiên cá, tiên nhỏ. Qua đó các bé học được những bài học nhỏ về lòng dũng cảm, tình bạn và việc dọn nhà rất vui!! Bộ sách gồm 3 cuốn: Gấu Hoggs dũng cảm, Úm ba la ánh sáng hiện ra!, Úm ba la biến ra nhà sạch! Giá sản phẩm trên Tiki đã bao gồm thuế theo luật hiện hành.",
//                images8,
//                45000.0,
//                4.5,
//                10,
//                20,
//                1,
//                "2025-04-30 14:20:45",
//                "Tô Bảo"
//        ));
//
//        // Book 9
//        List<Image> images9 = new ArrayList<>();
//        images9.add(new Image("https://salt.tikicdn.com/ts/product/e7/da/4a/8e75769f26664050a3f60fa150efb0f4.jpg",
//                "Cover for WHO? Chuyện Kể Về Danh Nhân Thế Giới"));
//        bookList.add(new Book(
//                9,
//                "WHO? Chuyện Kể Về Danh Nhân Thế Giới",
//                "WHO? Chuyện Kể Về Danh Nhân Thế Giới - Dành cho bé từ 6 - 15 tuổi Lời nói đầu Hồi nhỏ, Nikola Tesla đã vô cùng ngạc nhiên khi nhìn thấy tia sét trên bầu trời ngày mưa. Cậu bé ước mơ nghiên cứu về điện năm ấy về sau đã trở thành nhà khoa học, mở ra kỷ nguyên dòng điện xoay chiều bằng phát minh ra động cơ và máy phát điện xoay chiều. Nikola Tesla là nhà khoa học thiên tài không màng tới tiền bạc hay danh vọng, ông dành trọn cuộc đời cống hiến vì lợi ích của nhân loại. Cuốn sách này kể về những danh nhân thế giới, từ các nhà khoa học, nghệ sĩ đến những nhà lãnh đạo, giúp trẻ em hiểu về cuộc đời và đóng góp của họ.",
//                images9,
//                52000.0,
//                4.2,
//                8,
//                12,
//                3,
//                "2025-05-01 09:30:00",
//                "Nguyễn Sơn"
//        ));
//
//        // Book 10
//        List<Image> images10 = new ArrayList<>();
//        images10.add(new Image("https://salt.tikicdn.com/ts/product/67/77/6e/915e36b7629c4792218f19b57a8868e4.jpg",
//                "Cover for 100 Kỹ Năng Sinh Tồn"));
//        bookList.add(new Book(
//                10,
//                "100 Kỹ Năng Sinh Tồn",
//                "100 Kỹ Năng Sinh Tồn Giới thiệu tác giả Clint Emerson là một cựu Đặc vụ SEAL, ông đã dành hai mươi năm chỉ đạo những chiến dịch đặc biệt trên khắp thế giới trong lúc còn Titans bó với Đội Ba SEAL, Cơ quan An ninh Quốc gia Hoa Kỳ (NSA), và Đội Sáu SEAL tinh nhuệ. Tốt nghiệp trường Đại học Quân sự Hoa Kỳ ở Virginia với tấm bằng cử nhân về Quản lý An ninh, Clint Emerson đã dành nhiều năm trong lực lượng tác chiến đặc biệt này với một Đơn vị Nhiệm vụ Đặc biệt SMU, phát triển và thực hiện các kỹ năng chuyên môn để hỗ trợ các hoạt động trong những môi trường khắc nghiệt khác nhau nhằm vào các cá nhân xuất sắc. Là một chuyên gia giám sát và chống khủng bố, Clint Emerson đã nhận được rất nhiều giải thưởng cho lòng dũng cảm và sự lãnh đạo của mình.",
//                images10,
//                30000.0,
//                3.8,
//                3,
//                18,
//                3,
//                "2025-05-02 16:45:10",
//                "Clint Emerson" // Corrected from Nguyễn Nhật Ánh
//        ));
        // Filter books by category ID
        if (categoryId == 0) {
            filteredBookList.addAll(bookList); // Show all books if categoryId is 0
        } else {
            for (Book book : bookList) {
                if (book.getCategoryId() == categoryId) {
                    filteredBookList.add(book);
                }
            }
        }

        // Setup adapter
        listBookAdapter = new BookAdapter(this, filteredBookList, new ArrayList<>()); // Pass filtered list

        listBookRecyclerView.setHasFixedSize(true);
        // Set layout manager and adapter
        listBookRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        listBookRecyclerView.setAdapter(listBookAdapter);
    }
}