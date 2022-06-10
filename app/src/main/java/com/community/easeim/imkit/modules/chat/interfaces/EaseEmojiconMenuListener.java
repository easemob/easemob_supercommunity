package com.community.easeim.imkit.modules.chat.interfaces;


public interface EaseEmojiconMenuListener{
        /**
         * on emojicon clicked
         * @param emojicon
         */
        void onExpressionClicked(Object emojicon);
        /**
         * on delete image clicked
         */
        default void onDeleteImageClicked() {}
}