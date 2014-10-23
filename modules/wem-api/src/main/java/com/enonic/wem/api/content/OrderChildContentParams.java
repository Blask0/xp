package com.enonic.wem.api.content;

public class OrderChildContentParams
{
    private final ContentId contentToMove;

    private final ContentId contentToMoveBefore;

    private OrderChildContentParams( final Builder builder )
    {
        contentToMove = builder.contentToMove;
        contentToMoveBefore = builder.contentToMoveBefore;
    }


    public ContentId getContentToMove()
    {
        return contentToMove;
    }

    public ContentId getContentToMoveBefore()
    {
        return contentToMoveBefore;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ContentId contentToMove;

        private ContentId contentToMoveBefore;

        private Builder()
        {
        }

        public Builder contentToMove( ContentId contentToMove )
        {
            this.contentToMove = contentToMove;
            return this;
        }

        public Builder contentToMoveBefore( ContentId contentToMoveBefore )
        {
            this.contentToMoveBefore = contentToMoveBefore;
            return this;
        }

        public OrderChildContentParams build()
        {
            return new OrderChildContentParams( this );
        }
    }
}
