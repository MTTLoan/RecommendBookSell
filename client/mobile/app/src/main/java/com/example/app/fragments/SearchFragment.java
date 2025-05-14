package com.example.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.adapters.CategoryAdapter;
import com.example.app.R;
import com.example.app.activities.ListBookActivity;
import com.example.app.adapters.BookAdapter;
import com.example.app.adapters.CategoryAdapter;
import com.example.app.models.Book;
import com.example.app.models.Category;
import com.example.app.models.Image;
import com.example.app.utils.HeaderController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private ImageView btnFilter;
    private RecyclerView searchResultsRecyclerView;
    private RecyclerView recommendationsRecyclerView;
    private RecyclerView categoryRecyclerView;
    private BookAdapter searchResultsAdapter;
    private BookAdapter recommendationsAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Book> bookList;
    private List<Category> categoryList;
    private TextView noResultsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // Initialize views
        searchView = view.findViewById(R.id.searchView);
        btnFilter = view.findViewById(R.id.btnFilter);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        recommendationsRecyclerView = view.findViewById(R.id.recommendationsRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        noResultsText = view.findViewById(R.id.noResultsText);

        // Initialize data
        initData(view);

        // Set up RecyclerView for header
        HeaderController.setupHeader(requireActivity());

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup SearchView
        setupSearchView();

        // Setup Filter Button
        setupFilterButton();

        // Optional: Clear search when back button is pressed
        searchView.setOnCloseListener(() -> {
            clearSearch();
            return false; // Return false to allow default behavior
        });

        return view;
    }

    private void initData(View view) {
        // Sample category data
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Sách thiếu nhi", "Fictional books", "https://cdn.baolaocai.vn/images/27cb6a3dc6bec6a269f35257fe14bcd812e6142cc69115636526ea totals/c/1-4750.jpg"));
        categoryList.add(new Category(2, "Sách văn học", "Non-fictional books", "https://product.hstatic.net/1000237375/product/thiet_ke_chua_co_ten__52__cf9dd8d7f0b0416fa7d7691daa840b8e_master.png"));
        categoryList.add(new Category(3, "Sách kinh tế", "Science books", "https://cdnphoto.dantri.com.vn/-y7D2Z9iYLqKVO9e1QRpXH7CHnY=/zoom/1200_630/2023/10/12/img5095-1697109683474.jpg"));

        // Sample book data
        List<Book> bookList = new ArrayList<>();

        // DateTimeFormatter for parsing "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Book 1
        List<Image> images1 = new ArrayList<>();
        images1.add(new Image("https://salt.tikicdn.com/ts/product/73/24/11/1d84888511d73e6f5da2057115dcc4d8.png",
                "1d84888511d73e6f5da2057115dcc4d8.png"));
        LocalDateTime createdAt1 = LocalDateTime.parse("2025-04-28 02:27:21", formatter);
        bookList.add(new Book(
                1,
                "Cùng con trưởng thành - Mình không thích bị cô lập",
                "Lời nói đầu   Bé mới tầm 1 tuổi đã cần đọc sách chưa? ...",
                images1,
                28000.0,
                3.5,
                2,
                10,
                2,
                createdAt1,
                "Tô Bảo"
        ));

        // Book 2
        List<Image> images2 = new ArrayList<>();
        images2.add(new Image("https://salt.tikicdn.com/ts/product/5b/21/12/3d905ef72b7de07171761e4b1819543c.jpg",
                "789abc123def456ghi789jkl.png"));
        LocalDateTime createdAt2 = LocalDateTime.parse("2025-04-29 10:15:30", formatter);
        bookList.add(new Book(
                2,
                "Rèn luyện Kỹ Năng Sống dành cho học sinh - 25 thói quen tốt để thành công",
                "Rèn Luyện Kĩ Năng Sống Dành Cho Học Sinh ...",
                images2,
                35000.0,
                4.0,
                5,
                15,
                2,
                createdAt2,
                "Nguyễn Nhật Ánh"
        ));

        // Book 3
        List<Image> images3 = new ArrayList<>();
        images3.add(new Image("https://salt.tikicdn.com/ts/product/16/72/77/dff96564663b63ba96b2c74b60261dcd.jpg",
                "345mno678pqr901stu234vwx.png"));
        LocalDateTime createdAt3 = LocalDateTime.parse("2025-04-30 14:20:45", formatter);
        bookList.add(new Book(
                3,
                "Xứ Sở Miên Man",
                "Xứ Sở Miên Man   Giới thiệu tác giả ...",
                images3,
                45000.0,
                4.5,
                10,
                20,
                2,
                createdAt3,
                "Nhật Sơn"
        ));

        // Book 4
        List<Image> images4 = new ArrayList<>();
        images4.add(new Image("https://salt.tikicdn.com/ts/product/56/bc/59/f63f4561ee47a86e1843e671fc6355e5.jpg",
                "123yz456abc789def012ghi.png"));
        LocalDateTime createdAt4 = LocalDateTime.parse("2025-05-01 09:30:00", formatter);
        bookList.add(new Book(
                4,
                "Tuổi Thơ Dữ Dội - Tập 2",
                "“Tuổi Thơ Dữ Dội” là một câu chuyện hay ...",
                images4,
                52000.0,
                4.2,
                8,
                12,
                2,
                createdAt4,
                "Mai Anh"
        ));

        // Book 5
        List<Image> images5 = new ArrayList<>();
        images5.add(new Image("https://salt.tikicdn.com/ts/product/0f/f9/70/e273b6980de4f6f550329aafe91578d8.jpg",
                "567jkl890mno123pqr456stu.png"));
        LocalDateTime createdAt5 = LocalDateTime.parse("2025-05-02 16:45:10", formatter);
        bookList.add(new Book(
                5,
                "Búp Sen Xanh",
                "Câu chuyện khoa học về vòng tuần hoàn của nước...",
                images5,
                30000.0,
                3.8,
                3,
                18,
                3,
                createdAt5,
                "Sơn Tùng"
        ));

        // Book 6
        List<Image> images6 = new ArrayList<>();
        images6.add(new Image("https://salt.tikicdn.com/ts/product/f2/01/28/35b7bf7dcaf02091c69fbbd4f9bb929f.jpg",
                "1d84888511d73e6f5da2057115dcc4d8.png"));
        LocalDateTime createdAt6 = LocalDateTime.parse("2025-04-28 02:27:21", formatter);
        bookList.add(new Book(
                6,
                "Chuyện Con Mèo Dạy Hải Âu Bay",
                "Sinh năm 1949 tại Chile ...",
                images6,
                28000.0,
                3.5,
                2,
                10,
                2,
                createdAt6,
                "Nguyễn Nhật Ánh"
        ));

        // Book 7
        List<Image> images7 = new ArrayList<>();
        images7.add(new Image("https://salt.tikicdn.com/ts/product/75/96/cf/8be7ccb29bb999c9b9aed8e65c75b291.jpg",
                "789abc123def456ghi789jkl.png"));
        LocalDateTime createdAt7 = LocalDateTime.parse("2025-04-29 10:15:30", formatter);
        bookList.add(new Book(
                7,
                "Những Con Mèo Sau Bức Tường Hoa",
                "Thông tin sản phẩm ...",
                images7,
                35000.0,
                4.0,
                5,
                15,
                1,
                createdAt7,
                "Hà Mi"
        ));

        // Book 8
        List<Image> images8 = new ArrayList<>();
        images8.add(new Image("https://salt.tikicdn.com/ts/product/a7/24/37/42434f74d352fade0090a0d3790b0e9b.jpg",
                "345mno678pqr901stu234vwx.png"));
        LocalDateTime createdAt8 = LocalDateTime.parse("2025-04-30 14:20:45", formatter);
        bookList.add(new Book(
                8,
                "Bộ ba phép thuật - Úm ba la ánh sáng hiện ra",
                "Bộ sách tranh kể về chuyến phiêu lưu ...",
                images8,
                45000.0,
                4.5,
                10,
                20,
                1,
                createdAt8,
                "Tô Bảo"
        ));

        // Book 9
        List<Image> images9 = new ArrayList<>();
        images9.add(new Image("https://salt.tikicdn.com/ts/product/e7/da/4a/8e75769f26664050a3f60fa150efb0f4.jpg",
                "123yz456abc789def012ghi.png"));
        LocalDateTime createdAt9 = LocalDateTime.parse("2025-05-01 09:30:00", formatter);
        bookList.add(new Book(
                9,
                "WHO? Chuyện Kể Về Danh Nhân Thế Giới",
                "\"WHO? Chuyện Kể Về Danh Nhân Thế Giới ...",
                images9,
                52000.0,
                4.2,
                8,
                12,
                3,
                createdAt9,
                "Nguyễn Sơn"
        ));

        // Book 10
        List<Image> images10 = new ArrayList<>();
        images10.add(new Image("https://salt.tikicdn.com/ts/product/67/77/6e/915e36b7629c4792218f19b57a8868e4.jpg",
                "567jkl890mno123pqr456stu.png"));
        LocalDateTime createdAt10 = LocalDateTime.parse("2025-05-02 16:45:10", formatter);
        bookList.add(new Book(
                10,
                "100 Kỹ Năng Sinh Tồn",
                "\"100 Kỹ Năng Sinh Tồn ...",
                images10,
                30000.0,
                3.8,
                3,
                18,
                3,
                createdAt10,
                "Nguyễn Nhật Ánh"
        ));
        // Set up click listener for recommendationsTitle
        TextView recommendationsTitle = view.findViewById(R.id.recommendationsTitle);
        if (recommendationsTitle != null) {
            recommendationsTitle.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), ListBookActivity.class);
                intent.putExtra("category_id", 1); // 0 for "Đề xuất dành riêng cho bạn" (all books)
                intent.putExtra("category_name", "Đề xuất dành riêng cho bạn");
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerViews() {
        // Search Results RecyclerView (2 books per row)
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columns
        searchResultsAdapter = new BookAdapter(requireContext(), new ArrayList<>(), categoryList); // Start with empty list
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Recommendations RecyclerView (initially shows all books)
        recommendationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendationsAdapter = new BookAdapter(requireContext(), bookList, categoryList);
        recommendationsRecyclerView.setAdapter(recommendationsAdapter);

        // Category RecyclerView
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                searchView.setBackgroundColor(getResources().getColor(R.color.search_background_focused));
            } else {
                searchView.setBackgroundColor(getResources().getColor(R.color.light_dark));
            }
        });
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Filter clicked", Toast.LENGTH_SHORT).show();
            // Implement filter dialog or activity here
        });
    }

    private void filterBooks(String query) {
        List<Book> filteredList = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getName().toLowerCase().contains(query.toLowerCase()) || book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }

        if (query.isEmpty()) {
            clearSearch(); // Clear search results when query is empty
        } else {
            if (filteredList.isEmpty()) {
                noResultsText.setVisibility(View.VISIBLE);
                searchResultsRecyclerView.setVisibility(View.GONE);
            } else {
                noResultsText.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.VISIBLE);
                searchResultsAdapter.updateList(filteredList);
            }
        }
    }

    private void clearSearch() {
        noResultsText.setVisibility(View.GONE);
        searchResultsRecyclerView.setVisibility(View.GONE);
        searchResultsAdapter.updateList(new ArrayList<>());
        searchView.clearFocus();
        searchView.setQuery("", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!searchView.getQuery().toString().isEmpty()) {
            clearSearch();
        }
    }
}