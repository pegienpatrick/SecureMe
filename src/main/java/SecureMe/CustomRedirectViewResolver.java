package SecureMe;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.RedirectView;

public class CustomRedirectViewResolver extends UrlBasedViewResolver {

    public CustomRedirectViewResolver() {
        setViewClass(RedirectView.class);
    }

    @Override
    protected View createView(String viewName, java.util.Locale locale) throws Exception {
        if (viewName.startsWith("redirect:")) {
            String redirectUrl = viewName.substring("redirect:".length());
            RedirectView redirectView = (RedirectView) super.createView(redirectUrl, locale);
            assert redirectView != null;
            redirectView.setUrl(getPrefix() + redirectView.getUrl());
            return redirectView;
        } else {
            return super.createView(viewName, locale);
        }
    }
}
