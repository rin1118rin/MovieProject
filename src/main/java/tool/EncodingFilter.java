package tool;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

@WebFilter(urlPatterns={"/*"})
public class EncodingFilter implements Filter {

	public void doFilter(
		ServletRequest request, ServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		System.out.println("フィルタの前処理");
		
		//chain.doFilter(request, response);
		try {
            chain.doFilter(request, response);
        } catch (Throwable t) {// ★ ここで何が起きても必ず表示
            t.printStackTrace();// Eclipse のコンソールに例外の詳細が出る
            if (t instanceof ServletException) {
                throw (ServletException) t;
            } else if (t instanceof IOException) {
                throw (IOException) t;
            } else {
                throw new ServletException(t);
            }
        }
		System.out.println("フィルタの後処理");
//      HttpServletRequest req = (HttpServletRequest) request;
//      HttpServletResponse res = (HttpServletResponse) response;
//      // HTML にフィルタの前処理を表示
//      PrintWriter out = res.getWriter();
//      out.println("<p style='color:red;'>[フィルタ] 前処理: " + req.getRequestURI() + "</p>");
//      out.flush(); // すぐにブラウザに表示
//
//      // chain.doFilter() で次の処理 (index.html など) へ進む
//      chain.doFilter(request, response);
//
//      res.flushBuffer();
//
//
//      // ★ フィルタの後処理を 2 秒遅延
//      try {
//          Thread.sleep(2000); // 2秒待機
//      } catch (InterruptedException e) {
//          e.printStackTrace();
//      }
//      // HTML にフィルタの後処理を表示
//      out.println("<p style='color:blue;'>[フィルタ] 後処理: " + req.getRequestURI() + "</p>");
//      out.flush(); // すぐにブラウザに表示
	}

	public void init(FilterConfig filterConfig) {}
	public void destroy() {}
}
