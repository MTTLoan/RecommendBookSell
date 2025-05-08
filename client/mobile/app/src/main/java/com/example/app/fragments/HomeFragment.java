package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app.R;
import com.example.app.activities.ListBookActivity;
import com.example.app.adapters.BannerAdapter;
import com.example.app.adapters.BookAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.utils.HeaderController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private BannerAdapter bannerAdapter;
    private List<Integer> imageList;
    private Handler handler = new Handler();
    private Runnable autoSlideRunnable;

    private RecyclerView recommendationsRecyclerView, economicDealsRecyclerView;
    private RecyclerView bestDealsRecyclerView;
    private BookAdapter recommendationsAdapter;
    private BookAdapter bestDealsAdapter, economicDealsAdapter;
    private List<Book> bookList;
    private List<Category> categoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        // Set up RecyclerView for header
        HeaderController.setupHeader(requireActivity());

        viewPager2 = view.findViewById(R.id.bannerSlider);
        imageList = new ArrayList<>();

        // Add images to the list (replace with actual resource IDs)
        imageList.add(R.drawable.banner1);
        imageList.add(R.drawable.banner2);
        imageList.add(R.drawable.banner3);

        bannerAdapter = new BannerAdapter(imageList);
        viewPager2.setAdapter(bannerAdapter);

        // Auto slide functionality
        startAutoSlide();

        // Initialize RecyclerViews
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        bestDealsRecyclerView = view.findViewById(R.id.bestDealsRecyclerView);
        economicDealsRecyclerView = view.findViewById(R.id.economicDealsRecyclerView);

        // Setup layout managers
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestDealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        economicDealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Initialize category list
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Sách thiếu nhi", "Fictional books", "https://example.com/category_fiction.jpg"));
        categoryList.add(new Category(2, "Sách văn học", "Non-fictional books", "https://example.com/category_nonfiction.jpg"));
        categoryList.add(new Category(3, "Sách kinh tế", "Science books", "https://example.com/category_science.jpg"));

        // Sample book data (increased to test scrolling)
        bookList = new ArrayList<>();
        List<Book.Image> images1 = new ArrayList<>();
        images1.add(new Book.Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Cùng con trưởng thành - Mình không thích bị cô lập", "Lời nói đầu   Bé mới tầm 1 tuổi đã cần đọc sách chưa?   Bố mẹ muốn cho bé tiếp xúc sớm với sách được chứ?   Bé còn nhỏ quá, sợ cầm sách thì xé mất!   Bé mới ít tuổi thế đã nhận thức được gì chưa mà đọc sách?   Đúng là trẻ nhỏ chưa cần “đọc” sách, mà các con cần được “chơi” với sách như là một món đồ chơi giàu tính tương tác. Sách cho lứa tuổi dưới 3 thường phải đảm bảo các tiêu chí: giấy dày để trẻ khó xé hay cắn hỏng, hình ảnh rất to rõ và thường ít màu để phù hợp với khả năng tiếp thu của trẻ, có hiệu ứng tương tác như lật giở-sờ chạm… để trẻ được vận động tay chân với sách, in bằng màu tốt và phủ bóng để đảm bảo an toàn nếu trẻ lỡ cắn hay liếm sách, nội dung thật gần gũi với nhu cầu và tâm lý lứa tuổi để bố mẹ dễ nói chuyện thủ thỉ với trẻ nhằm tăng cường giao tiếp thân mật…", images1, 28000.0, 3.5, 2, 10, 2, "2025-04-28 02:27:21", "Tô Bảo"));

        List<Book.Image> images2 = new ArrayList<>();
        images2.add(new Book.Image("https://salt.tikicdn.com/ts/product/5b/21/12/3d905ef72b7de07171761e4b1819543c.jpg", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Rèn luyện Kỹ Năng Sống dành cho học sinh - 25 thói quen tốt để thành công", "Rèn Luyện Kĩ Năng Sống Dành Cho Học Sinh - 25 Thói Quen Tốt Để Thành Công   Giới thiệu tác phẩm   - Hãy động não, thay đổi cách suy nghĩ, bạn có thể là một học sinh thiên tài!   - Hãy để thói quen tốt chiến thắng thói quen xấu để trở thành một người thành công!   Dành tặng tất cả những trẻ em mơ ước trở thành thiên tài   Lười biếng, mất vệ sinh, làm việc chậm chạp lề mề… Các bạn nhỏ có phải có những thói quen khiến người khác bực mình? Đừng coi thường sự ảnh hưởng của thói quen xấu này nhé! Chúng giống như chiếc kẹo cao su, khi đã dính vào người sẽ rất khó dứt ra đấy. Bắt đầu từ bây giờ, hãy học cách coi thói quen tốt như bạn thân của mình và loại bỏ thói quen xấu đi, các bạn nhé! ", images2, 35000.0, 4.0, 5, 15, 2, "2025-04-29 10:15:30", "Nguyễn Nhật Ánh"));

        List<Book.Image> images3 = new ArrayList<>();
        images3.add(new Book.Image("https://salt.tikicdn.com/ts/product/16/72/77/dff96564663b63ba96b2c74b60261dcd.jpg", "345mno678pqr901stu234vwx.png"));
        bookList.add(new Book(3, "Xứ Sở Miên Man", "Xứ Sở Miên Man   Giới thiệu tác giả     Jun Phạm , tên thật là Phạm Duy Thuận, không chỉ được biết đến là một ca sĩ, diễn viên tài năng mà còn là một nhà văn trẻ đầy triển vọng. Anh từng là thành viên của nhóm nhạc 365daband, một trong những nhóm nhạc nổi tiếng nhất Việt Nam. Tuy nhiên, bên cạnh đam mê ca hát, Jun Phạm còn có một tình yêu sâu sắc với văn chương.   Jun Phạm bắt đầu con đường văn chương từ rất sớm. Anh thường xuyên chia sẻ những suy nghĩ, cảm xúc của mình trên trang cá nhân và nhận được sự ủng hộ nhiệt tình từ người hâm mộ. Chính điều này đã thôi thúc anh cho ra đời những cuốn sách của mình.", images3, 45000.0, 4.5, 10, 20, 2, "2025-04-30 14:20:45", "Nhật Sơn"));

        List<Book.Image> images4 = new ArrayList<>();
        images4.add(new Book.Image("https://salt.tikicdn.com/ts/product/56/bc/59/f63f4561ee47a86e1843e671fc6355e5.jpg", "123yz456abc789def012ghi.png"));
        bookList.add(new Book(4, "Tuổi Thơ Dữ Dội - Tập 2", "“Tuổi Thơ Dữ Dội” là một câu chuyện hay, cảm động viết về tuổi thơ. Sách dày 404 trang mà người đọc không bao giờ muốn ngừng lại, bị lôi cuốn vì những nhân vật ngây thơ có, khôn ranh có, anh hùng có, vì những sự việc khi thì ly kỳ, khi thì hài hước, khi thì gây xúc động đến ứa nước mắ \\\"Tuổi Thơ Dữ Dội” không phải chỉ là một câu chuyện cổ tích, mà là một câu chuyện có thật ở trần gian, ở đó, những con người tuổi nhỏ đã tham gia vào cuộc kháng chiến chống xâm lược bảo vệ Tổ quốc với một chuỗi những chiến công đầy ắp li kì và hấp dẫn.", images4, 52000.0, 4.2, 8, 12, 2, "2025-05-01 09:30:00", "Mai Anh"));

        List<Book.Image> images5 = new ArrayList<>();
        images5.add(new Book.Image("https://salt.tikicdn.com/ts/product/0f/f9/70/e273b6980de4f6f550329aafe91578d8.jpg", "567jkl890mno123pqr456stu.png"));
        bookList.add(new Book(5, "Búp Sen Xanh", "Câu chuyện khoa học về vòng tuần hoàn của nước...", images5, 30000.0, 3.8, 3, 18, 3, "2025-05-02 16:45:10", "Sơn Tùng"));

        // Book 1
        List<Book.Image> images6 = new ArrayList<>();
        images6.add(new Book.Image("https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg", "1d84888511d73e6f5da2057115dcc4d8.png"));
        bookList.add(new Book(1, "Chuyện Con Mèo Dạy Hải Âu Bay", "Sinh năm 1949 tại Chile, Luis Sepúlveda đã trải qua những năm tháng đầy biến động dưới chế độ độc tài của Augusto Pinochet. Chính những trải nghiệm sống thực tế đã trở thành nguồn cảm hứng lớn cho các tác phẩm của ông. Sepúlveda bắt đầu sự nghiệp viết lách từ năm 17 tuổi, nhưng phải đến những năm 1980, tên tuổi của ông mới thực sự được biết đến rộng rãi với tiểu thuyết Lão già mê đọc truyện tình. Cuốn sách này đã nhanh chóng trở thành một hiện tượng văn học, với hơn 18 triệu bản được bán ra trên toàn thế giới.   Lời nói đầu/Giới thiệu sách   Chuyện con mèo dạy hải âu bay là kiệt tác dành cho thiếu nhi của nhà văn Chi Lê nổi tiếng Luis Sepúlveda – tác giả của cuốn Lão già mê đọc truyện tình đã bán được 18 triệu bản khắp thế giới.", images1, 28000.0, 3.5, 2, 10, 2, "2025-04-28 02:27:21", "Nguyễn Nhật Ánh"));

        // Book 2
        List<Book.Image> images7 = new ArrayList<>();
        images7.add(new Book.Image("https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg", "789abc123def456ghi789jkl.png"));
        bookList.add(new Book(2, "Những Con Mèo Sau Bức Tường Hoa", "Thông tin sản phẩm   NHỮNG CON MÈO SAU BỨC TƯỜNG HOA  Tác giả: Hà Mi; Minh họa: Trần Lê Nguyên Hà  Thể loại: Truyện thiếu nhi;  Số trang: 176 trang Khổ: 13x18 cm Giá: 69.000đ  ISBN: 978-604-490-293-7 – Mã:8935069923897  Nhà xuất bản Phụ nữ Việt Nam phát hành trên toàn quốc tháng 9 năm 2024   Giới thiệu sách   Những con mèo sau bức tường hoa, truyện thiếu nhi lọt vào vòng chung khảo giải thưởng Dế mèn, là câu chuyện về một gia đình nhỏ nằm sau bức tường hoa huỳnh vàng. Trong căn nhà ấy có đôi vợ chồng trung niên sống cùng sáu chú mèo: Sasa, Lulu, Fufu, Tom, Cục Mỡ, và Coco.    Bạn sẽ nghĩ đôi vợ chồng ấy – ông bà Bắp Cải – hẳn rất yêu mèo mới có thể nuôi nhiều mèo đến vậy. Nhưng không, ban đầu hai ông bà vốn chẳng yêu mèo.", images2, 35000.0, 4.0, 5, 15, 1, "2025-04-29 10:15:30", "Hà Mi"));

        // Book 3
        List<Book.Image> images8 = new ArrayList<>();
        images8.add(new Book.Image("https://salt.tikicdn.com/ts/product/a7/24/37/42434f74d352fade0090a0d3790b0e9b.jpg", "345mno678pqr901stu234vwx.png"));
        bookList.add(new Book(3, "Bộ ba phép thuật - Úm ba la ánh sáng hiện ra", "Bộ sách tranh kể về chuyến phiêu lưu trong thế giới phép thuật của gấu Hoggs cùng hai người bạn chồn Poki và thỏ Fips. Bộ ba cưỡi thảm thần, học thần chú, gặp những nhân vật có phép thuật như phù thủy, nàng tiên cá, tiên nhỏ, Qua đó các bé học được những bài học nhỏ về lòng dũng cảm, tình bạn và việc dọn nhà rất vui!!   Bộ sách gồm 3 cuốn:     Gấu Hoggs dũng cảm   Úm ba la ánh sáng hiện ra!   Úm ba la biến ra nhà sạch!   Giá sản phẩm trên Tiki đã bao gồm thuế theo luật hiện hành.", images3, 45000.0, 4.5, 10, 20, 1, "2025-04-30 14:20:45", "Tô Bảo"));

        // Book 4
        List<Book.Image> images9 = new ArrayList<>();
        images9.add(new Book.Image("https://salt.tikicdn.com/ts/product/e7/da/4a/8e75769f26664050a3f60fa150efb0f4.jpg", "123yz456abc789def012ghi.png"));
        bookList.add(new Book(4, "WHO? Chuyện Kể Về Danh Nhân Thế Giới", "\"WHO? Chuyện Kể Về Danh Nhân Thế Giới - Dành cho bé từ 6 - 15 tuổi   Lời nói đầu     Hồi nhỏ, Nikola Tesla đã vô cùng ngạc nhiên khi nhìn thấy tia sét trên bầu trời ngày mưa. Cậu bé ước mơ nghiên cứu về điện năm ấy về sau đã trở thành nhà khoa học, mở ra kỉ nguyên dòng điện xoay chiều bằng phát minh ra động cơ và máy phát điện xoay chiều. Nikola Tesla là nhà khoa học thiên tài không màng tới tiền bạc hay danh vọng, ông dành trọn cuộc đời cống hiến vì lợi ích của nhân loại.", images4, 52000.0, 4.2, 8, 12, 3, "2025-05-01 09:30:00", "Nguyễn Sơn"));

        // Book 5
        List<Book.Image> images10 = new ArrayList<>();
        images10.add(new Book.Image("https://salt.tikicdn.com/ts/product/67/77/6e/915e36b7629c4792218f19b57a8868e4.jpg", "567jkl890mno123pqr456stu.png"));
        bookList.add(new Book(5, "100 Kỹ Năng Sinh Tồn", "\"100 Kỹ Năng Sinh Tồn   Giới thiệu tác giả     Clint Emerson là một cựu Đặc vụ SEAL, ông đã dành hai mươi năm chỉ đạo những chiến dịch đặc biệt trên khắp thế giới trong lúc còn gắn bó với Đội Ba SEAL, Cơ quan An ninh Quốc gia Hoa Kỳ (NSA), và Đội Sáu SEAL tinh nhuệ. Tốt nghiệp trường Đại học Quân sự Hoa Kỳ ở Virginia với tấm bằng cử nhân về Quản lý An ninh, Clint Emerson đã dành nhiều năm trong lực lượng tác chiến đặc biệt này với một Đơn vị Nhiệm vụ Đặc biệt SMU, phát triển và thực hiện các kỹ năng chuyên môn để hỗ trợ các hoạt động trong những môi trường khắc nghiệt khác nhau nhằm vào các cá nhân xuất sắc. Là một chuyên gia giám sát và chống khủng bố, Clint Emerson đã nhận được rất nhiều giải thưởng cho lòng dũng cảm và sự lãnh đạo của mình.", images5, 30000.0, 3.8, 3, 18, 3, "2025-05-02 16:45:10", "Nguyễn Nhật Ánh"));

        // Filter books for recommendations (all books for "Đề xuất dành riêng cho bạn")
        List<Book> recommendedBooks = bookList.stream().filter(book -> book.getCategoryId() == 1).collect(Collectors.toList());

        // Filter books for "Best Deals" (e.g., top 3 books by sold count from category 2 - "Sách văn học")
        List<Book> bestDealsBooks = bookList.stream()
                .filter(book -> book.getCategoryId() == 2)
//                .sorted(Comparator.comparingInt(Book::getSold).reversed()) // Sort by sold count descending
//                .limit(3) // Take top 3
                .collect(Collectors.toList());

        // Filter books for "economic Deals" (e.g., top 3 books by sold count from category 1 - "Sách thiếu nhi")
        List<Book> economicDealsBooks = bookList.stream()
                .filter(book -> book.getCategoryId() == 3)
                .collect(Collectors.toList());

        // Set up click listeners for category titles
        view.findViewById(R.id.recommendationsTitle).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListBookActivity.class);
            intent.putExtra("category_id", 1); // 0 for "Đề xuất dành riêng cho bạn" (all books)
            intent.putExtra("category_name", "Đề xuất dành riêng cho bạn");
            startActivity(intent);
        });

        view.findViewById(R.id.bestDealsTitle).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListBookActivity.class);
            intent.putExtra("category_id", 2); // Category ID for "Sách văn học"
            intent.putExtra("category_name", "Sách văn học");
            startActivity(intent);
        });

        view.findViewById(R.id.economicDealsTitle).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListBookActivity.class);
            intent.putExtra("category_id", 3); // Corrected to category ID 1 for "Sách thiếu nhi"
            intent.putExtra("category_name", "Sách thiếu nhi");
            startActivity(intent);
        });

        // Setup adapters with filtered lists
        recommendationsAdapter = new BookAdapter(requireContext(), recommendedBooks, categoryList);
        bestDealsAdapter = new BookAdapter(requireContext(), bestDealsBooks, categoryList);
        economicDealsAdapter = new BookAdapter(requireContext(), economicDealsBooks, categoryList);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);
        bestDealsRecyclerView.setAdapter(bestDealsAdapter);
        economicDealsRecyclerView.setAdapter(economicDealsAdapter);

        return view;
    }

    private void startAutoSlide() {
        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % imageList.size();
                viewPager2.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 4000); // Auto slide every 4 seconds
            }
        };
        handler.postDelayed(autoSlideRunnable, 4000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(autoSlideRunnable); // Stop auto slide when fragment is paused
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(autoSlideRunnable, 4000); // Restart auto slide when fragment is resumed
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(autoSlideRunnable); // Clean up handler when fragment view is destroyed
    }
}