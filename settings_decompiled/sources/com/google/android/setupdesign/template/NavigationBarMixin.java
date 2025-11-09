package com.google.android.setupdesign.template;

import android.view.View;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.Mixin;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.view.NavigationBar;
/* loaded from: classes2.dex */
public class NavigationBarMixin implements Mixin {
    private final TemplateLayout templateLayout;

    public NavigationBarMixin(TemplateLayout templateLayout) {
        this.templateLayout = templateLayout;
    }

    public NavigationBar getNavigationBar() {
        View findManagedViewById = this.templateLayout.findManagedViewById(R$id.sud_layout_navigation_bar);
        if (findManagedViewById instanceof NavigationBar) {
            return (NavigationBar) findManagedViewById;
        }
        return null;
    }
}
