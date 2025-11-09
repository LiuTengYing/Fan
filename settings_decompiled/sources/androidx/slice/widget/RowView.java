package androidx.slice.widget;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.ViewCompat;
import androidx.slice.CornerDrawable;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$id;
import androidx.slice.view.R$layout;
import androidx.slice.view.R$plurals;
import androidx.slice.view.R$style;
import androidx.slice.widget.SliceActionView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class RowView extends SliceChildView implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final boolean sCanSpecifyLargerRangeBarHeight = true;
    private final View mActionDivider;
    private final ProgressBar mActionSpinner;
    private final ArrayMap<SliceActionImpl, SliceActionView> mActions;
    private boolean mAllowTwoLines;
    private final View mBottomDivider;
    private final LinearLayout mContent;
    private final LinearLayout mEndContainer;
    Handler mHandler;
    private List<SliceAction> mHeaderActions;
    private int mIconSize;
    private int mImageSize;
    private boolean mIsHeader;
    boolean mIsRangeSliding;
    private boolean mIsStarRating;
    long mLastSentRangeUpdate;
    private final TextView mLastUpdatedText;
    protected Set<SliceItem> mLoadingActions;
    private int mMeasuredRangeHeight;
    private final TextView mPrimaryText;
    private View mRangeBar;
    boolean mRangeHasPendingUpdate;
    private SliceItem mRangeItem;
    int mRangeMaxValue;
    int mRangeMinValue;
    Runnable mRangeUpdater;
    boolean mRangeUpdaterRunning;
    int mRangeValue;
    private final RatingBar.OnRatingBarChangeListener mRatingBarChangeListener;
    private final LinearLayout mRootView;
    private SliceActionImpl mRowAction;
    RowContent mRowContent;
    int mRowIndex;
    private final TextView mSecondaryText;
    private View mSeeMoreView;
    private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;
    private SliceItem mSelectionItem;
    private ArrayList<String> mSelectionOptionKeys;
    private ArrayList<CharSequence> mSelectionOptionValues;
    private Spinner mSelectionSpinner;
    boolean mShowActionSpinner;
    private final LinearLayout mStartContainer;
    private SliceItem mStartItem;
    private final LinearLayout mSubContent;
    private final ArrayMap<SliceActionImpl, SliceActionView> mToggles;

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public RowView(Context context) {
        super(context);
        this.mToggles = new ArrayMap<>();
        this.mActions = new ArrayMap<>();
        this.mLoadingActions = new HashSet();
        this.mRangeUpdater = new Runnable() { // from class: androidx.slice.widget.RowView.2
            @Override // java.lang.Runnable
            public void run() {
                RowView.this.sendSliderValue();
                RowView.this.mRangeUpdaterRunning = false;
            }
        };
        this.mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() { // from class: androidx.slice.widget.RowView.3
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                RowView rowView = RowView.this;
                rowView.mRangeValue = i + rowView.mRangeMinValue;
                long currentTimeMillis = System.currentTimeMillis();
                RowView rowView2 = RowView.this;
                long j = rowView2.mLastSentRangeUpdate;
                if (j != 0 && currentTimeMillis - j > 200) {
                    rowView2.mRangeUpdaterRunning = false;
                    rowView2.mHandler.removeCallbacks(rowView2.mRangeUpdater);
                    RowView.this.sendSliderValue();
                } else if (rowView2.mRangeUpdaterRunning) {
                } else {
                    rowView2.mRangeUpdaterRunning = true;
                    rowView2.mHandler.postDelayed(rowView2.mRangeUpdater, 200L);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                RowView.this.mIsRangeSliding = true;
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                RowView rowView = RowView.this;
                rowView.mIsRangeSliding = false;
                if (rowView.mRangeUpdaterRunning || rowView.mRangeHasPendingUpdate) {
                    rowView.mRangeUpdaterRunning = false;
                    rowView.mRangeHasPendingUpdate = false;
                    rowView.mHandler.removeCallbacks(rowView.mRangeUpdater);
                    RowView rowView2 = RowView.this;
                    int progress = seekBar.getProgress();
                    RowView rowView3 = RowView.this;
                    rowView2.mRangeValue = progress + rowView3.mRangeMinValue;
                    rowView3.sendSliderValue();
                }
            }
        };
        this.mRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() { // from class: androidx.slice.widget.RowView.4
            @Override // android.widget.RatingBar.OnRatingBarChangeListener
            public void onRatingChanged(RatingBar ratingBar, float f, boolean z) {
                RowView rowView = RowView.this;
                rowView.mRangeValue = Math.round(f + rowView.mRangeMinValue);
                long currentTimeMillis = System.currentTimeMillis();
                RowView rowView2 = RowView.this;
                long j = rowView2.mLastSentRangeUpdate;
                if (j != 0 && currentTimeMillis - j > 200) {
                    rowView2.mRangeUpdaterRunning = false;
                    rowView2.mHandler.removeCallbacks(rowView2.mRangeUpdater);
                    RowView.this.sendSliderValue();
                } else if (rowView2.mRangeUpdaterRunning) {
                } else {
                    rowView2.mRangeUpdaterRunning = true;
                    rowView2.mHandler.postDelayed(rowView2.mRangeUpdater, 200L);
                }
            }
        };
        this.mIconSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mImageSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R$layout.abc_slice_small_template, (ViewGroup) this, false);
        this.mRootView = linearLayout;
        addView(linearLayout);
        this.mStartContainer = (LinearLayout) findViewById(R$id.icon_frame);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(16908290);
        this.mContent = linearLayout2;
        this.mSubContent = (LinearLayout) findViewById(R$id.subcontent);
        this.mPrimaryText = (TextView) findViewById(16908310);
        this.mSecondaryText = (TextView) findViewById(16908304);
        this.mLastUpdatedText = (TextView) findViewById(R$id.last_updated);
        this.mBottomDivider = findViewById(R$id.bottom_divider);
        this.mActionDivider = findViewById(R$id.action_divider);
        ProgressBar progressBar = (ProgressBar) findViewById(R$id.action_sent_indicator);
        this.mActionSpinner = progressBar;
        SliceViewUtil.tintIndeterminateProgressBar(getContext(), progressBar);
        this.mEndContainer = (LinearLayout) findViewById(16908312);
        ViewCompat.setImportantForAccessibility(this, 2);
        ViewCompat.setImportantForAccessibility(linearLayout2, 2);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setStyle(SliceStyle sliceStyle, RowStyle rowStyle) {
        super.setStyle(sliceStyle, rowStyle);
        applyRowStyle();
    }

    private void applyRowStyle() {
        RowStyle rowStyle;
        if (this.mSliceStyle == null || (rowStyle = this.mRowStyle) == null) {
            return;
        }
        setViewSidePaddings(this.mStartContainer, rowStyle.getTitleItemStartPadding(), this.mRowStyle.getTitleItemEndPadding());
        setViewSidePaddings(this.mContent, this.mRowStyle.getContentStartPadding(), this.mRowStyle.getContentEndPadding());
        setViewSidePaddings(this.mPrimaryText, this.mRowStyle.getTitleStartPadding(), this.mRowStyle.getTitleEndPadding());
        setViewSidePaddings(this.mSubContent, this.mRowStyle.getSubContentStartPadding(), this.mRowStyle.getSubContentEndPadding());
        setViewSidePaddings(this.mEndContainer, this.mRowStyle.getEndItemStartPadding(), this.mRowStyle.getEndItemEndPadding());
        setViewSideMargins(this.mBottomDivider, this.mRowStyle.getBottomDividerStartPadding(), this.mRowStyle.getBottomDividerEndPadding());
        setViewHeight(this.mActionDivider, this.mRowStyle.getActionDividerHeight());
        if (this.mRowStyle.getTintColor() != -1) {
            setTint(this.mRowStyle.getTintColor());
        }
    }

    private void setViewSidePaddings(View view, int i, int i2) {
        boolean z = i < 0 && i2 < 0;
        if (view == null || z) {
            return;
        }
        if (i < 0) {
            i = view.getPaddingStart();
        }
        int paddingTop = view.getPaddingTop();
        if (i2 < 0) {
            i2 = view.getPaddingEnd();
        }
        view.setPaddingRelative(i, paddingTop, i2, view.getPaddingBottom());
    }

    private void setViewSideMargins(View view, int i, int i2) {
        boolean z = i < 0 && i2 < 0;
        if (view == null || z) {
            return;
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (i >= 0) {
            marginLayoutParams.setMarginStart(i);
        }
        if (i2 >= 0) {
            marginLayoutParams.setMarginEnd(i2);
        }
        view.setLayoutParams(marginLayoutParams);
    }

    private void setViewHeight(View view, int i) {
        if (view == null || i < 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = i;
        view.setLayoutParams(layoutParams);
    }

    private void setViewWidth(View view, int i) {
        if (view == null || i < 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = i;
        view.setLayoutParams(layoutParams);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setInsets(int i, int i2, int i3, int i4) {
        super.setInsets(i, i2, i3, i4);
        setPadding(i, i2, i3, i4);
    }

    protected SliceItem getPrimaryActionItem() {
        RowContent rowContent = this.mRowContent;
        if (rowContent != null) {
            return rowContent.getPrimaryAction();
        }
        return null;
    }

    protected String getPrimaryActionKey() {
        SliceItem primaryAction;
        RowContent rowContent = this.mRowContent;
        if (rowContent == null || (primaryAction = rowContent.getPrimaryAction()) == null || primaryAction == this.mStartItem) {
            return null;
        }
        SliceActionImpl sliceActionImpl = new SliceActionImpl(primaryAction);
        this.mRowAction = sliceActionImpl;
        return sliceActionImpl.getKey();
    }

    protected List<String> getEndItemKeys() {
        ArrayList arrayList = new ArrayList();
        RowContent rowContent = this.mRowContent;
        if (rowContent != null) {
            List<SliceItem> endItems = rowContent.getEndItems();
            for (int i = 0; i < endItems.size(); i++) {
                SliceActionImpl sliceActionImpl = new SliceActionImpl(endItems.get(i));
                if (sliceActionImpl.getKey() != null) {
                    arrayList.add(sliceActionImpl.getKey());
                }
            }
        }
        return arrayList;
    }

    private int getRowContentHeight() {
        int height = this.mRowContent.getHeight(this.mSliceStyle, this.mViewPolicy);
        if (this.mRangeBar != null && this.mStartItem == null) {
            height -= this.mSliceStyle.getRowRangeHeight();
        }
        return this.mSelectionSpinner != null ? height - this.mSliceStyle.getRowSelectionHeight() : height;
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setTint(int i) {
        super.setTint(i);
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceActions(List<SliceAction> list) {
        this.mHeaderActions = list;
        if (this.mRowContent != null) {
            updateEndItems();
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setShowLastUpdated(boolean z) {
        super.setShowLastUpdated(z);
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setAllowTwoLines(boolean z) {
        this.mAllowTwoLines = z;
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    private void measureChildWithExactHeight(View view, int i, int i2) {
        measureChild(view, i, View.MeasureSpec.makeMeasureSpec(i2 + this.mInsetTop + this.mInsetBottom, 1073741824));
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int rowContentHeight = getRowContentHeight();
        if (rowContentHeight != 0) {
            this.mRootView.setVisibility(0);
            measureChildWithExactHeight(this.mRootView, i, rowContentHeight);
            i3 = this.mRootView.getMeasuredWidth();
        } else {
            this.mRootView.setVisibility(8);
            i3 = 0;
        }
        View view = this.mRangeBar;
        if (view != null && this.mStartItem == null) {
            if (sCanSpecifyLargerRangeBarHeight) {
                measureChildWithExactHeight(view, i, this.mSliceStyle.getRowRangeHeight());
            } else {
                measureChild(view, i, View.MeasureSpec.makeMeasureSpec(0, 0));
            }
            this.mMeasuredRangeHeight = this.mRangeBar.getMeasuredHeight();
            i3 = Math.max(i3, this.mRangeBar.getMeasuredWidth());
        } else {
            Spinner spinner = this.mSelectionSpinner;
            if (spinner != null) {
                measureChildWithExactHeight(spinner, i, this.mSliceStyle.getRowSelectionHeight());
                i3 = Math.max(i3, this.mSelectionSpinner.getMeasuredWidth());
            }
        }
        int max = Math.max(i3 + this.mInsetStart + this.mInsetEnd, getSuggestedMinimumWidth());
        RowContent rowContent = this.mRowContent;
        setMeasuredDimension(FrameLayout.resolveSizeAndState(max, i, 0), (rowContent != null ? rowContent.getHeight(this.mSliceStyle, this.mViewPolicy) : 0) + this.mInsetTop + this.mInsetBottom);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        LinearLayout linearLayout = this.mRootView;
        linearLayout.layout(paddingLeft, this.mInsetTop, linearLayout.getMeasuredWidth() + paddingLeft, getRowContentHeight() + this.mInsetTop);
        if (this.mRangeBar != null && this.mStartItem == null) {
            int rowContentHeight = getRowContentHeight() + ((this.mSliceStyle.getRowRangeHeight() - this.mMeasuredRangeHeight) / 2) + this.mInsetTop;
            View view = this.mRangeBar;
            view.layout(paddingLeft, rowContentHeight, view.getMeasuredWidth() + paddingLeft, this.mMeasuredRangeHeight + rowContentHeight);
        } else if (this.mSelectionSpinner != null) {
            int rowContentHeight2 = getRowContentHeight() + this.mInsetTop;
            Spinner spinner = this.mSelectionSpinner;
            spinner.layout(paddingLeft, rowContentHeight2, spinner.getMeasuredWidth() + paddingLeft, this.mSelectionSpinner.getMeasuredHeight() + rowContentHeight2);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0053, code lost:
        if (r2 != false) goto L23;
     */
    @Override // androidx.slice.widget.SliceChildView
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setSliceItem(androidx.slice.widget.SliceContent r5, boolean r6, int r7, int r8, androidx.slice.widget.SliceView.OnSliceActionListener r9) {
        /*
            r4 = this;
            r4.setSliceActionListener(r9)
            r8 = 0
            if (r5 == 0) goto L56
            androidx.slice.widget.RowContent r9 = r4.mRowContent
            if (r9 == 0) goto L56
            boolean r9 = r9.isValid()
            if (r9 == 0) goto L56
            androidx.slice.widget.RowContent r9 = r4.mRowContent
            if (r9 == 0) goto L1e
            androidx.slice.SliceStructure r0 = new androidx.slice.SliceStructure
            androidx.slice.SliceItem r9 = r9.getSliceItem()
            r0.<init>(r9)
            goto L1f
        L1e:
            r0 = 0
        L1f:
            androidx.slice.SliceStructure r9 = new androidx.slice.SliceStructure
            androidx.slice.SliceItem r1 = r5.getSliceItem()
            androidx.slice.Slice r1 = r1.getSlice()
            r9.<init>(r1)
            r1 = 1
            if (r0 == 0) goto L37
            boolean r2 = r0.equals(r9)
            if (r2 == 0) goto L37
            r2 = r1
            goto L38
        L37:
            r2 = r8
        L38:
            if (r0 == 0) goto L50
            android.net.Uri r3 = r0.getUri()
            if (r3 == 0) goto L50
            android.net.Uri r0 = r0.getUri()
            android.net.Uri r9 = r9.getUri()
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L50
            r9 = r1
            goto L51
        L50:
            r9 = r8
        L51:
            if (r9 == 0) goto L56
            if (r2 == 0) goto L56
            goto L57
        L56:
            r1 = r8
        L57:
            r4.mShowActionSpinner = r8
            r4.mIsHeader = r6
            androidx.slice.widget.RowContent r5 = (androidx.slice.widget.RowContent) r5
            r4.mRowContent = r5
            r4.mRowIndex = r7
            r4.populateViews(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.setSliceItem(androidx.slice.widget.SliceContent, boolean, int, int, androidx.slice.widget.SliceView$OnSliceActionListener):void");
    }

    private void populateViews(boolean z) {
        int titleSize;
        boolean z2 = z && this.mIsRangeSliding;
        if (!z2) {
            resetViewState();
        }
        char c = 65535;
        if (this.mRowContent.getLayoutDir() != -1) {
            setLayoutDirection(this.mRowContent.getLayoutDir());
        }
        if (this.mRowContent.isDefaultSeeMore()) {
            showSeeMore();
            return;
        }
        CharSequence contentDescription = this.mRowContent.getContentDescription();
        if (contentDescription != null) {
            this.mContent.setContentDescription(contentDescription);
        }
        SliceItem startItem = this.mRowContent.getStartItem();
        this.mStartItem = startItem;
        boolean z3 = startItem != null && (!this.mRowContent.getIsHeader() || this.mRowContent.hasTitleItems());
        if (z3) {
            z3 = addItem(this.mStartItem, this.mTintColor, true);
        }
        this.mStartContainer.setVisibility(z3 ? 0 : 8);
        SliceItem titleItem = this.mRowContent.getTitleItem();
        if (titleItem != null) {
            this.mPrimaryText.setText(titleItem.getSanitizedText());
        }
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null) {
            TextView textView = this.mPrimaryText;
            if (this.mIsHeader) {
                titleSize = sliceStyle.getHeaderTitleSize();
            } else {
                titleSize = sliceStyle.getTitleSize();
            }
            textView.setTextSize(0, titleSize);
            this.mPrimaryText.setTextColor(this.mRowStyle.getTitleColor());
        }
        this.mPrimaryText.setVisibility(titleItem != null ? 0 : 8);
        addSubtitle(titleItem != null);
        this.mBottomDivider.setVisibility(this.mRowContent.hasBottomDivider() ? 0 : 8);
        SliceItem primaryAction = this.mRowContent.getPrimaryAction();
        if (primaryAction != null && primaryAction != this.mStartItem) {
            SliceActionImpl sliceActionImpl = new SliceActionImpl(primaryAction);
            this.mRowAction = sliceActionImpl;
            if (sliceActionImpl.getSubtype() != null) {
                String subtype = this.mRowAction.getSubtype();
                subtype.hashCode();
                switch (subtype.hashCode()) {
                    case -868304044:
                        if (subtype.equals("toggle")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 759128640:
                        if (subtype.equals("time_picker")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 1250407999:
                        if (subtype.equals("date_picker")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        addAction(this.mRowAction, this.mTintColor, this.mEndContainer, false);
                        setViewClickable(this.mRootView, true);
                        return;
                    case 1:
                        setViewClickable(this.mRootView, true);
                        return;
                    case 2:
                        setViewClickable(this.mRootView, true);
                        return;
                }
            }
        }
        SliceItem range = this.mRowContent.getRange();
        if (range != null) {
            if (this.mRowAction != null) {
                setViewClickable(this.mRootView, true);
            }
            this.mRangeItem = range;
            SliceItem findSubtype = SliceQuery.findSubtype(range, "int", "range_mode");
            if (findSubtype != null) {
                this.mIsStarRating = findSubtype.getInt() == 2;
            }
            if (!z2) {
                initRangeBar();
                addRangeView();
            }
            if (this.mStartItem == null) {
                return;
            }
        }
        SliceItem selection = this.mRowContent.getSelection();
        if (selection != null) {
            this.mSelectionItem = selection;
            addSelection(selection);
            return;
        }
        updateEndItems();
        updateActionSpinner();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:96:0x014a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateEndItems() {
        /*
            Method dump skipped, instructions count: 335
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.updateEndItems():void");
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setLastUpdated(long j) {
        super.setLastUpdated(j);
        RowContent rowContent = this.mRowContent;
        if (rowContent != null) {
            addSubtitle(rowContent.getTitleItem() != null && TextUtils.isEmpty(this.mRowContent.getTitleItem().getSanitizedText()));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0064  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00fd  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0107  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x012e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void addSubtitle(boolean r10) {
        /*
            Method dump skipped, instructions count: 322
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.addSubtitle(boolean):void");
    }

    private CharSequence getRelativeTimeString(long j) {
        long currentTimeMillis = System.currentTimeMillis() - j;
        if (currentTimeMillis > 31449600000L) {
            int i = (int) (currentTimeMillis / 31449600000L);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_years, i, Integer.valueOf(i));
        } else if (currentTimeMillis > 86400000) {
            int i2 = (int) (currentTimeMillis / 86400000);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_days, i2, Integer.valueOf(i2));
        } else if (currentTimeMillis > 60000) {
            int i3 = (int) (currentTimeMillis / 60000);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_min, i3, Integer.valueOf(i3));
        } else {
            return null;
        }
    }

    private void initRangeBar() {
        SliceItem findSubtype = SliceQuery.findSubtype(this.mRangeItem, "int", "min");
        int i = findSubtype != null ? findSubtype.getInt() : 0;
        this.mRangeMinValue = i;
        SliceItem findSubtype2 = SliceQuery.findSubtype(this.mRangeItem, "int", "max");
        int i2 = this.mIsStarRating ? 5 : 100;
        if (findSubtype2 != null) {
            i2 = findSubtype2.getInt();
        }
        this.mRangeMaxValue = i2;
        SliceItem findSubtype3 = SliceQuery.findSubtype(this.mRangeItem, "int", "value");
        this.mRangeValue = findSubtype3 != null ? findSubtype3.getInt() - i : 0;
    }

    private void addRangeView() {
        ProgressBar progressBar;
        Drawable wrap;
        Drawable loadDrawable;
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        if (this.mIsStarRating) {
            addRatingBarView();
            return;
        }
        SliceItem findSubtype = SliceQuery.findSubtype(this.mRangeItem, "int", "range_mode");
        boolean z = findSubtype != null && findSubtype.getInt() == 1;
        boolean equals = "action".equals(this.mRangeItem.getFormat());
        boolean z2 = this.mStartItem == null;
        if (!equals) {
            if (z2) {
                progressBar = new ProgressBar(getContext(), null, 16842872);
            } else {
                progressBar = (ProgressBar) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_progress_inline_view, (ViewGroup) this, false);
                RowStyle rowStyle = this.mRowStyle;
                if (rowStyle != null) {
                    setViewWidth(progressBar, rowStyle.getProgressBarInlineWidth());
                    setViewSidePaddings(progressBar, this.mRowStyle.getProgressBarStartPadding(), this.mRowStyle.getProgressBarEndPadding());
                }
            }
            if (z) {
                progressBar.setIndeterminate(true);
            }
        } else if (z2) {
            progressBar = new SeekBar(getContext());
        } else {
            progressBar = (SeekBar) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_seekbar_view, (ViewGroup) this, false);
            RowStyle rowStyle2 = this.mRowStyle;
            if (rowStyle2 != null) {
                setViewWidth(progressBar, rowStyle2.getSeekBarInlineWidth());
            }
        }
        if (z) {
            wrap = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        } else {
            wrap = DrawableCompat.wrap(progressBar.getProgressDrawable());
        }
        int i = this.mTintColor;
        if (i != -1 && wrap != null) {
            DrawableCompat.setTint(wrap, i);
            if (z) {
                progressBar.setIndeterminateDrawable(wrap);
            } else {
                progressBar.setProgressDrawable(wrap);
            }
        }
        progressBar.setMax(this.mRangeMaxValue - this.mRangeMinValue);
        progressBar.setProgress(this.mRangeValue);
        progressBar.setVisibility(0);
        if (this.mStartItem == null) {
            addView(progressBar, new FrameLayout.LayoutParams(-1, -2));
        } else {
            this.mSubContent.setVisibility(8);
            this.mContent.addView(progressBar, 1);
        }
        this.mRangeBar = progressBar;
        if (equals) {
            SliceItem inputRangeThumb = this.mRowContent.getInputRangeThumb();
            SeekBar seekBar = (SeekBar) this.mRangeBar;
            if (inputRangeThumb != null && inputRangeThumb.getIcon() != null && (loadDrawable = inputRangeThumb.getIcon().loadDrawable(getContext())) != null) {
                seekBar.setThumb(loadDrawable);
            }
            Drawable wrap2 = DrawableCompat.wrap(seekBar.getThumb());
            int i2 = this.mTintColor;
            if (i2 != -1 && wrap2 != null) {
                DrawableCompat.setTint(wrap2, i2);
                seekBar.setThumb(wrap2);
            }
            seekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        }
    }

    private void addRatingBarView() {
        RatingBar ratingBar = new RatingBar(getContext());
        ((LayerDrawable) ratingBar.getProgressDrawable()).getDrawable(2).setColorFilter(this.mTintColor, PorterDuff.Mode.SRC_IN);
        ratingBar.setStepSize(1.0f);
        ratingBar.setNumStars(this.mRangeMaxValue);
        ratingBar.setRating(this.mRangeValue);
        ratingBar.setVisibility(0);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(17);
        linearLayout.setVisibility(0);
        linearLayout.addView(ratingBar, new FrameLayout.LayoutParams(-2, -2));
        addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        ratingBar.setOnRatingBarChangeListener(this.mRatingBarChangeListener);
        this.mRangeBar = linearLayout;
    }

    void sendSliderValue() {
        if (this.mRangeItem == null) {
            return;
        }
        try {
            this.mLastSentRangeUpdate = System.currentTimeMillis();
            this.mRangeItem.fireAction(getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.RANGE_VALUE", this.mRangeValue));
            if (this.mObserver != null) {
                EventInfo eventInfo = new EventInfo(getMode(), 2, 4, this.mRowIndex);
                eventInfo.state = this.mRangeValue;
                this.mObserver.onSliceAction(eventInfo, this.mRangeItem);
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e("RowView", "PendingIntent for slice cannot be sent", e);
        }
    }

    private void addSelection(SliceItem sliceItem) {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mSelectionOptionKeys = new ArrayList<>();
        this.mSelectionOptionValues = new ArrayList<>();
        List<SliceItem> items = sliceItem.getSlice().getItems();
        for (int i = 0; i < items.size(); i++) {
            SliceItem sliceItem2 = items.get(i);
            if (sliceItem2.hasHint("selection_option")) {
                SliceItem findSubtype = SliceQuery.findSubtype(sliceItem2, "text", "selection_option_key");
                SliceItem findSubtype2 = SliceQuery.findSubtype(sliceItem2, "text", "selection_option_value");
                if (findSubtype != null && findSubtype2 != null) {
                    this.mSelectionOptionKeys.add(findSubtype.getText().toString());
                    this.mSelectionOptionValues.add(findSubtype2.getSanitizedText());
                }
            }
        }
        this.mSelectionSpinner = (Spinner) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_row_selection, (ViewGroup) this, false);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R$layout.abc_slice_row_selection_text, this.mSelectionOptionValues);
        arrayAdapter.setDropDownViewResource(R$layout.abc_slice_row_selection_dropdown_text);
        this.mSelectionSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        addView(this.mSelectionSpinner);
        this.mSelectionSpinner.setOnItemSelectedListener(this);
    }

    private void addAction(SliceActionImpl sliceActionImpl, int i, ViewGroup viewGroup, boolean z) {
        SliceActionView sliceActionView = new SliceActionView(getContext(), this.mSliceStyle, this.mRowStyle);
        viewGroup.addView(sliceActionView);
        if (viewGroup.getVisibility() == 8) {
            viewGroup.setVisibility(0);
        }
        boolean isToggle = sliceActionImpl.isToggle();
        EventInfo eventInfo = new EventInfo(getMode(), !isToggle, isToggle != 0 ? 3 : 0, this.mRowIndex);
        if (z) {
            eventInfo.setPosition(0, 0, 1);
        }
        sliceActionView.setAction(sliceActionImpl, eventInfo, this.mObserver, i, this.mLoadingListener);
        if (this.mLoadingActions.contains(sliceActionImpl.getSliceItem())) {
            sliceActionView.setLoading(true);
        }
        if (isToggle != 0) {
            this.mToggles.put(sliceActionImpl, sliceActionView);
        } else {
            this.mActions.put(sliceActionImpl, sliceActionView);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean addItem(SliceItem sliceItem, int i, boolean z) {
        IconCompat iconCompat;
        SliceItem sliceItem2;
        SliceStyle sliceStyle;
        int i2;
        int i3;
        ViewGroup viewGroup = z ? this.mStartContainer : this.mEndContainer;
        if ("slice".equals(sliceItem.getFormat()) || "action".equals(sliceItem.getFormat())) {
            if (sliceItem.hasHint("shortcut")) {
                addAction(new SliceActionImpl(sliceItem), i, viewGroup, z);
                return true;
            } else if (sliceItem.getSlice().getItems().size() == 0) {
                return false;
            } else {
                sliceItem = sliceItem.getSlice().getItems().get(0);
            }
        }
        TextView textView = null;
        if ("image".equals(sliceItem.getFormat())) {
            iconCompat = sliceItem.getIcon();
            sliceItem2 = null;
        } else if ("long".equals(sliceItem.getFormat())) {
            sliceItem2 = sliceItem;
            iconCompat = null;
        } else {
            iconCompat = null;
            sliceItem2 = null;
        }
        if (iconCompat != null) {
            boolean z2 = !sliceItem.hasHint("no_tint");
            boolean hasHint = sliceItem.hasHint("raw");
            float f = getResources().getDisplayMetrics().density;
            ImageView imageView = new ImageView(getContext());
            Drawable loadDrawable = iconCompat.loadDrawable(getContext());
            SliceStyle sliceStyle2 = this.mSliceStyle;
            if ((sliceStyle2 != null && sliceStyle2.getApplyCornerRadiusToLargeImages()) && sliceItem.hasHint("large")) {
                imageView.setImageDrawable(new CornerDrawable(loadDrawable, this.mSliceStyle.getImageCornerRadius()));
            } else {
                imageView.setImageDrawable(loadDrawable);
            }
            if (z2 && i != -1) {
                imageView.setColorFilter(i);
            }
            if (this.mIsRangeSliding) {
                viewGroup.removeAllViews();
                viewGroup.addView(imageView);
            } else {
                viewGroup.addView(imageView);
            }
            RowStyle rowStyle = this.mRowStyle;
            if (rowStyle != null) {
                int iconSize = rowStyle.getIconSize();
                if (iconSize <= 0) {
                    iconSize = this.mIconSize;
                }
                this.mIconSize = iconSize;
                int imageSize = this.mRowStyle.getImageSize();
                if (imageSize <= 0) {
                    imageSize = this.mImageSize;
                }
                this.mImageSize = imageSize;
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = hasHint ? Math.round(loadDrawable.getIntrinsicWidth() / f) : this.mImageSize;
            if (hasHint) {
                i2 = Math.round(loadDrawable.getIntrinsicHeight() / f);
            } else {
                i2 = this.mImageSize;
            }
            layoutParams.height = i2;
            imageView.setLayoutParams(layoutParams);
            if (z2) {
                int i4 = this.mImageSize;
                i3 = i4 == -1 ? this.mIconSize / 2 : (i4 - this.mIconSize) / 2;
            } else {
                i3 = 0;
            }
            imageView.setPadding(i3, i3, i3, i3);
            textView = imageView;
        } else if (sliceItem2 != null) {
            textView = new TextView(getContext());
            textView.setText(SliceViewUtil.getTimestampString(getContext(), sliceItem.getLong()));
            if (this.mSliceStyle != null) {
                textView.setTextSize(0, sliceStyle.getSubtitleSize());
                textView.setTextColor(this.mRowStyle.getSubtitleColor());
            }
            viewGroup.addView(textView);
        }
        return textView != null;
    }

    private void showSeeMore() {
        final Button button = (Button) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_row_show_more, (ViewGroup) this, false);
        button.setOnClickListener(new View.OnClickListener() { // from class: androidx.slice.widget.RowView.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    RowView rowView = RowView.this;
                    if (rowView.mObserver != null) {
                        EventInfo eventInfo = new EventInfo(rowView.getMode(), 4, 0, RowView.this.mRowIndex);
                        RowView rowView2 = RowView.this;
                        rowView2.mObserver.onSliceAction(eventInfo, rowView2.mRowContent.getSliceItem());
                    }
                    RowView rowView3 = RowView.this;
                    rowView3.mShowActionSpinner = rowView3.mRowContent.getSliceItem().fireActionInternal(RowView.this.getContext(), null);
                    RowView rowView4 = RowView.this;
                    if (rowView4.mShowActionSpinner) {
                        SliceActionView.SliceActionLoadingListener sliceActionLoadingListener = rowView4.mLoadingListener;
                        if (sliceActionLoadingListener != null) {
                            sliceActionLoadingListener.onSliceActionLoading(rowView4.mRowContent.getSliceItem(), RowView.this.mRowIndex);
                        }
                        RowView rowView5 = RowView.this;
                        rowView5.mLoadingActions.add(rowView5.mRowContent.getSliceItem());
                        button.setVisibility(8);
                    }
                    RowView.this.updateActionSpinner();
                } catch (PendingIntent.CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        });
        int i = this.mTintColor;
        if (i != -1) {
            button.setTextColor(i);
        }
        this.mSeeMoreView = button;
        this.mRootView.addView(button);
        if (this.mLoadingActions.contains(this.mRowContent.getSliceItem())) {
            this.mShowActionSpinner = true;
            button.setVisibility(8);
            updateActionSpinner();
        }
    }

    void updateActionSpinner() {
        this.mActionSpinner.setVisibility(this.mShowActionSpinner ? 0 : 8);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setLoadingActions(Set<SliceItem> set) {
        if (set == null) {
            this.mLoadingActions.clear();
            this.mShowActionSpinner = false;
        } else {
            this.mLoadingActions = set;
        }
        updateEndItems();
        updateActionSpinner();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        SliceActionView sliceActionView;
        SliceActionView.SliceActionLoadingListener sliceActionLoadingListener;
        SliceActionImpl sliceActionImpl = this.mRowAction;
        if (sliceActionImpl == null || sliceActionImpl.getActionItem() == null) {
            return;
        }
        if (this.mRowAction.getSubtype() != null) {
            String subtype = this.mRowAction.getSubtype();
            subtype.hashCode();
            char c = 65535;
            switch (subtype.hashCode()) {
                case -868304044:
                    if (subtype.equals("toggle")) {
                        c = 0;
                        break;
                    }
                    break;
                case 759128640:
                    if (subtype.equals("time_picker")) {
                        c = 1;
                        break;
                    }
                    break;
                case 1250407999:
                    if (subtype.equals("date_picker")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    sliceActionView = this.mToggles.get(this.mRowAction);
                    break;
                case 1:
                    onClickPicker(false);
                    return;
                case 2:
                    onClickPicker(true);
                    return;
                default:
                    sliceActionView = this.mActions.get(this.mRowAction);
                    break;
            }
        } else {
            sliceActionView = this.mActions.get(this.mRowAction);
        }
        if (sliceActionView != null && !(view instanceof SliceActionView)) {
            sliceActionView.sendAction();
        } else if (this.mRowContent.getIsHeader()) {
            performClick();
        } else {
            try {
                this.mShowActionSpinner = this.mRowAction.getActionItem().fireActionInternal(getContext(), null);
                if (this.mObserver != null) {
                    this.mObserver.onSliceAction(new EventInfo(getMode(), 3, 0, this.mRowIndex), this.mRowAction.getSliceItem());
                }
                if (this.mShowActionSpinner && (sliceActionLoadingListener = this.mLoadingListener) != null) {
                    sliceActionLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                    this.mLoadingActions.add(this.mRowAction.getSliceItem());
                }
                updateActionSpinner();
            } catch (PendingIntent.CanceledException e) {
                Log.e("RowView", "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    private void onClickPicker(boolean z) {
        if (this.mRowAction == null) {
            return;
        }
        Log.d("ASDF", "ASDF" + z + ":" + this.mRowAction.getSliceItem());
        SliceItem findSubtype = SliceQuery.findSubtype(this.mRowAction.getSliceItem(), "long", "millis");
        if (findSubtype == null) {
            return;
        }
        int i = this.mRowIndex;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(findSubtype.getLong()));
        if (z) {
            new DatePickerDialog(getContext(), R$style.DialogTheme, new DateSetListener(this.mRowAction.getSliceItem(), i), calendar.get(1), calendar.get(2), calendar.get(5)).show();
        } else {
            new TimePickerDialog(getContext(), R$style.DialogTheme, new TimeSetListener(this.mRowAction.getSliceItem(), i), calendar.get(11), calendar.get(12), false).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DateSetListener implements DatePickerDialog.OnDateSetListener {
        private final SliceItem mActionItem;
        private final int mRowIndex;

        DateSetListener(SliceItem sliceItem, int i) {
            this.mActionItem = sliceItem;
            this.mRowIndex = i;
        }

        @Override // android.app.DatePickerDialog.OnDateSetListener
        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(i, i2, i3);
            Date time = calendar.getTime();
            SliceItem sliceItem = this.mActionItem;
            if (sliceItem != null) {
                try {
                    sliceItem.fireAction(RowView.this.getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.RANGE_VALUE", time.getTime()));
                    RowView rowView = RowView.this;
                    if (rowView.mObserver != null) {
                        RowView.this.mObserver.onSliceAction(new EventInfo(rowView.getMode(), 6, 7, this.mRowIndex), this.mActionItem);
                    }
                } catch (PendingIntent.CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class TimeSetListener implements TimePickerDialog.OnTimeSetListener {
        private final SliceItem mActionItem;
        private final int mRowIndex;

        TimeSetListener(SliceItem sliceItem, int i) {
            this.mActionItem = sliceItem;
            this.mRowIndex = i;
        }

        @Override // android.app.TimePickerDialog.OnTimeSetListener
        public void onTimeSet(TimePicker timePicker, int i, int i2) {
            Date time = Calendar.getInstance().getTime();
            time.setHours(i);
            time.setMinutes(i2);
            SliceItem sliceItem = this.mActionItem;
            if (sliceItem != null) {
                try {
                    sliceItem.fireAction(RowView.this.getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.RANGE_VALUE", time.getTime()));
                    RowView rowView = RowView.this;
                    if (rowView.mObserver != null) {
                        RowView.this.mObserver.onSliceAction(new EventInfo(rowView.getMode(), 7, 8, this.mRowIndex), this.mActionItem);
                    }
                } catch (PendingIntent.CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        }
    }

    @Override // android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.mSelectionItem == null || adapterView != this.mSelectionSpinner || i < 0 || i >= this.mSelectionOptionKeys.size()) {
            return;
        }
        if (this.mObserver != null) {
            this.mObserver.onSliceAction(new EventInfo(getMode(), 5, 6, this.mRowIndex), this.mSelectionItem);
        }
        try {
            if (this.mSelectionItem.fireActionInternal(getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.SELECTION", this.mSelectionOptionKeys.get(i)))) {
                this.mShowActionSpinner = true;
                SliceActionView.SliceActionLoadingListener sliceActionLoadingListener = this.mLoadingListener;
                if (sliceActionLoadingListener != null) {
                    sliceActionLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                    this.mLoadingActions.add(this.mRowAction.getSliceItem());
                }
                updateActionSpinner();
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e("RowView", "PendingIntent for slice cannot be sent", e);
        }
    }

    private void setViewClickable(View view, boolean z) {
        view.setOnClickListener(z ? this : null);
        view.setBackground(z ? SliceViewUtil.getDrawable(getContext(), 16843534) : null);
        view.setClickable(z);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void resetView() {
        this.mRowContent = null;
        this.mLoadingActions.clear();
        resetViewState();
    }

    private void resetViewState() {
        this.mRootView.setVisibility(0);
        setLayoutDirection(2);
        setViewClickable(this.mRootView, false);
        setViewClickable(this.mContent, false);
        this.mStartContainer.removeAllViews();
        this.mEndContainer.removeAllViews();
        this.mEndContainer.setVisibility(8);
        this.mPrimaryText.setText((CharSequence) null);
        this.mSecondaryText.setText((CharSequence) null);
        this.mLastUpdatedText.setText((CharSequence) null);
        this.mLastUpdatedText.setVisibility(8);
        this.mToggles.clear();
        this.mActions.clear();
        this.mRowAction = null;
        this.mBottomDivider.setVisibility(8);
        this.mActionDivider.setVisibility(8);
        View view = this.mSeeMoreView;
        if (view != null) {
            this.mRootView.removeView(view);
            this.mSeeMoreView = null;
        }
        this.mIsRangeSliding = false;
        this.mRangeHasPendingUpdate = false;
        this.mRangeItem = null;
        this.mRangeMinValue = 0;
        this.mRangeMaxValue = 0;
        this.mRangeValue = 0;
        this.mLastSentRangeUpdate = 0L;
        this.mHandler = null;
        View view2 = this.mRangeBar;
        if (view2 != null) {
            if (this.mStartItem == null) {
                removeView(view2);
            } else {
                this.mContent.removeView(view2);
            }
            this.mRangeBar = null;
        }
        this.mSubContent.setVisibility(0);
        this.mStartItem = null;
        this.mActionSpinner.setVisibility(8);
        Spinner spinner = this.mSelectionSpinner;
        if (spinner != null) {
            removeView(spinner);
            this.mSelectionSpinner = null;
        }
        this.mSelectionItem = null;
    }
}
