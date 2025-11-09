package com.android.settings.network.telephony;

import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.util.List;
/* loaded from: classes.dex */
public final class NetworkModeChoicesProto$UiOptions extends GeneratedMessageLite<NetworkModeChoicesProto$UiOptions, Builder> implements MessageLiteOrBuilder {
    public static final int CHOICES_FIELD_NUMBER = 2;
    private static final NetworkModeChoicesProto$UiOptions DEFAULT_INSTANCE;
    public static final int FORMAT_FIELD_NUMBER = 3;
    private static volatile Parser<NetworkModeChoicesProto$UiOptions> PARSER = null;
    public static final int TYPE_FIELD_NUMBER = 1;
    private static final Internal.ListAdapter.Converter<Integer, PresentFormat> format_converter_ = new Internal.ListAdapter.Converter<Integer, PresentFormat>() { // from class: com.android.settings.network.telephony.NetworkModeChoicesProto$UiOptions.1
        @Override // com.google.protobuf.Internal.ListAdapter.Converter
        public PresentFormat convert(Integer num) {
            PresentFormat forNumber = PresentFormat.forNumber(num.intValue());
            return forNumber == null ? PresentFormat.PRESENT_FORMAT_UNSPECIFIED : forNumber;
        }
    };
    private int bitField0_;
    private int choices_;
    private int type_;
    private byte memoizedIsInitialized = 2;
    private Internal.IntList format_ = GeneratedMessageLite.emptyIntList();

    private NetworkModeChoicesProto$UiOptions() {
    }

    /* loaded from: classes.dex */
    public enum PresentFormat implements Internal.EnumLite {
        PRESENT_FORMAT_UNSPECIFIED(0),
        add1xEntry(1),
        add2gEntry(2),
        add3gEntry(3),
        addGlobalEntry(4),
        addWorldModeCdmaEntry(5),
        addWorldModeGsmEntry(6),
        add4gEntry(7),
        addLteEntry(8),
        add5gEntry(9),
        add5gAnd4gEntry(10),
        add5gAndLteEntry(11);
        
        private static final Internal.EnumLiteMap<PresentFormat> internalValueMap = new Internal.EnumLiteMap<PresentFormat>() { // from class: com.android.settings.network.telephony.NetworkModeChoicesProto.UiOptions.PresentFormat.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public PresentFormat findValueByNumber(int i) {
                return PresentFormat.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        public static PresentFormat forNumber(int i) {
            switch (i) {
                case 0:
                    return PRESENT_FORMAT_UNSPECIFIED;
                case 1:
                    return add1xEntry;
                case 2:
                    return add2gEntry;
                case 3:
                    return add3gEntry;
                case 4:
                    return addGlobalEntry;
                case 5:
                    return addWorldModeCdmaEntry;
                case 6:
                    return addWorldModeGsmEntry;
                case 7:
                    return add4gEntry;
                case 8:
                    return addLteEntry;
                case 9:
                    return add5gEntry;
                case 10:
                    return add5gAnd4gEntry;
                case 11:
                    return add5gAndLteEntry;
                default:
                    return null;
            }
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return PresentFormatVerifier.INSTANCE;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static final class PresentFormatVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new PresentFormatVerifier();

            private PresentFormatVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return PresentFormat.forNumber(i) != null;
            }
        }

        PresentFormat(int i) {
            this.value = i;
        }
    }

    public NetworkModeChoicesProto$EnabledNetworks getType() {
        NetworkModeChoicesProto$EnabledNetworks forNumber = NetworkModeChoicesProto$EnabledNetworks.forNumber(this.type_);
        return forNumber == null ? NetworkModeChoicesProto$EnabledNetworks.ENABLED_NETWORKS_UNSPECIFIED : forNumber;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setType(NetworkModeChoicesProto$EnabledNetworks networkModeChoicesProto$EnabledNetworks) {
        networkModeChoicesProto$EnabledNetworks.getClass();
        this.bitField0_ |= 1;
        this.type_ = networkModeChoicesProto$EnabledNetworks.getNumber();
    }

    public int getChoices() {
        return this.choices_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setChoices(int i) {
        this.bitField0_ |= 2;
        this.choices_ = i;
    }

    static {
        NetworkModeChoicesProto$UiOptions networkModeChoicesProto$UiOptions = new NetworkModeChoicesProto$UiOptions();
        DEFAULT_INSTANCE = networkModeChoicesProto$UiOptions;
        GeneratedMessageLite.registerDefaultInstance(NetworkModeChoicesProto$UiOptions.class, networkModeChoicesProto$UiOptions);
    }

    public List<PresentFormat> getFormatList() {
        return new Internal.ListAdapter(this.format_, format_converter_);
    }

    private void ensureFormatIsMutable() {
        if (this.format_.isModifiable()) {
            return;
        }
        this.format_ = GeneratedMessageLite.mutableCopy(this.format_);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addFormat(PresentFormat presentFormat) {
        presentFormat.getClass();
        ensureFormatIsMutable();
        this.format_.addInt(presentFormat.getNumber());
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    /* loaded from: classes.dex */
    public static final class Builder extends GeneratedMessageLite.Builder<NetworkModeChoicesProto$UiOptions, Builder> implements MessageLiteOrBuilder {
        private Builder() {
            super(NetworkModeChoicesProto$UiOptions.DEFAULT_INSTANCE);
        }

        public NetworkModeChoicesProto$EnabledNetworks getType() {
            return ((NetworkModeChoicesProto$UiOptions) this.instance).getType();
        }

        public Builder setType(NetworkModeChoicesProto$EnabledNetworks networkModeChoicesProto$EnabledNetworks) {
            copyOnWrite();
            ((NetworkModeChoicesProto$UiOptions) this.instance).setType(networkModeChoicesProto$EnabledNetworks);
            return this;
        }

        public int getChoices() {
            return ((NetworkModeChoicesProto$UiOptions) this.instance).getChoices();
        }

        public Builder setChoices(int i) {
            copyOnWrite();
            ((NetworkModeChoicesProto$UiOptions) this.instance).setChoices(i);
            return this;
        }

        public List<PresentFormat> getFormatList() {
            return ((NetworkModeChoicesProto$UiOptions) this.instance).getFormatList();
        }

        public Builder addFormat(PresentFormat presentFormat) {
            copyOnWrite();
            ((NetworkModeChoicesProto$UiOptions) this.instance).addFormat(presentFormat);
            return this;
        }
    }

    @Override // com.google.protobuf.GeneratedMessageLite
    protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        switch (NetworkModeChoicesProto$1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new NetworkModeChoicesProto$UiOptions();
            case 2:
                return new Builder();
            case 3:
                return GeneratedMessageLite.newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0003\u0000\u0001\u0001\u0003\u0003\u0000\u0001\u0002\u0001Ԍ\u0000\u0002Ԅ\u0001\u0003\u001e", new Object[]{"bitField0_", "type_", NetworkModeChoicesProto$EnabledNetworks.internalGetVerifier(), "choices_", "format_", PresentFormat.internalGetVerifier()});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<NetworkModeChoicesProto$UiOptions> parser = PARSER;
                if (parser == null) {
                    synchronized (NetworkModeChoicesProto$UiOptions.class) {
                        parser = PARSER;
                        if (parser == null) {
                            parser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                            PARSER = parser;
                        }
                    }
                }
                return parser;
            case 6:
                return Byte.valueOf(this.memoizedIsInitialized);
            case 7:
                this.memoizedIsInitialized = (byte) (obj == null ? 0 : 1);
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
