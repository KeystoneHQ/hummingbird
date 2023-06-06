package com.sparrowwallet.hummingbird.registry.extend;

import com.sparrowwallet.hummingbird.registry.RegistryItem;

public abstract class CallParams extends RegistryItem {
    public enum CallType {
        KeyDerivation;

        public static CallType fromOrdinal(int value) {
            if (value == 0) {
                return CallType.KeyDerivation;
            }
            throw new IllegalArgumentException("invalid call type: " + value);
        }
    }

    public abstract CallType getCallType();

}
