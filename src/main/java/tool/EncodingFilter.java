package tool;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

// すべてのリクエストに対して文字コード（UTF-8）を一括設定する
@WebFilter("/*") // 「/*」でアプリケーション内のすべてのURLアクセスに適用
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // フィルター初期化時の処理（必要に応じて記述）
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // リクエストの文字コードを UTF-8 に設定（POST送信などの文字化けを防止）
        request.setCharacterEncoding("UTF-8");
        
        // レスポンスの文字コードとコンテンツタイプを設定
        response.setCharacterEncoding("UTF-8");

        // 次の処理（FrontController、Action、JSP等）へ引き継ぐ
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // ↓必要な場合はコードをかいて（フィルター破棄時の処理）
    }
}