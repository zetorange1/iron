/*
 * Decompiled with CFR 0_118.
 */
package com.ironsource.sdk.data;

public class SSAEnums {

    public static enum ControllerState {
        None,
        FailedToDownload,
        FailedToLoad,
        Loaded,
        Ready,
        Failed;
        

        private ControllerState() {
        }
    }

    public static enum BackButtonState {
        None,
        Device,
        Controller;
        

        private BackButtonState() {
        }
    }

    public static enum ProductType {
        OfferWall,
        Interstitial,
        OfferWallCredits,
        RewardedVideo;
        

        private ProductType() {
        }
    }

    public static enum DebugMode {
        MODE_0(0),
        MODE_1(1),
        MODE_2(2),
        MODE_3(3);
        
        private int value;

        private DebugMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

}

