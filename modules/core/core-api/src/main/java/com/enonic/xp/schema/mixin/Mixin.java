package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.schema.BaseSchema;

@Beta
public final class Mixin
    extends BaseSchema<MixinName>
{
    private final Form form;

    private Mixin( final Builder builder )
    {
        super( builder );
        this.form = builder.formBuilder.build();
    }

    public Form getForm()
    {
        return this.form;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Mixin mixin )
    {
        return new Builder( mixin );
    }

    public static class Builder
        extends BaseSchema.Builder<Builder, MixinName>
    {
        private Form.Builder formBuilder = Form.create();

        public Builder()
        {
            super();
        }

        public Builder( final Mixin mixin )
        {
            super( mixin );
            this.formBuilder = Form.create( mixin.getForm() );
        }

        @Override
        public Builder name( final MixinName value )
        {
            super.name( value );
            return this;
        }

        public Builder name( final String value )
        {
            super.name( MixinName.from( value ) );
            return this;
        }

        public Builder form( final Form value )
        {
            this.formBuilder = Form.create( value );
            return this;
        }

        public Builder addFormItem( final FormItem value )
        {
            this.formBuilder.addFormItem( value );
            return this;
        }

        public Mixin build()
        {
            return new Mixin( this );
        }
    }
}
