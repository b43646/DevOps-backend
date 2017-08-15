package DevOps.domain;

/**
 * Created by renlo on 8/15/2017.
 */
public class CrumbIssuer {

    private String className;
    private String crumb;
    private String crumbRequestField;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCrumb() {
        return crumb;
    }

    public void setCrumb(String crumb) {
        this.crumb = crumb;
    }

    public String getCrumbRequestField() {
        return crumbRequestField;
    }

    public void setCrumbRequestField(String crumbRequestField) {
        this.crumbRequestField = crumbRequestField;
    }
}
