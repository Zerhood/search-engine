package main.service.indexing;

import java.util.concurrent.CopyOnWriteArrayList;

public class SiteParsing {
    private volatile SiteParsing parent;
    private final String url;
    private final CopyOnWriteArrayList<SiteParsing> children;

    public SiteParsing(String url) {
        this.url = url;
        parent = null;
        children = new CopyOnWriteArrayList<>();
    }

    public synchronized void addChild(SiteParsing element) {
        SiteParsing root = getRootElement();
        if (!root.contains(element.getUrl())) {
            element.setParent(this);
            children.add(element);
        }
    }

    private boolean contains(String url) {
        if (this.url.equals(url)) {
            return true;
        }
        for (SiteParsing child : children) {
            if (child.contains(url))
                return true;
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    private void setParent(SiteParsing sitemapNode) {
        synchronized (this) {
            this.parent = sitemapNode;
        }
    }

    public SiteParsing getRootElement() {
        return parent == null ? this : parent.getRootElement();
    }

    public CopyOnWriteArrayList<SiteParsing> getChildren() {
        return children;
    }

}