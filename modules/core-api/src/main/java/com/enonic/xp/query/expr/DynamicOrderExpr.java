package com.enonic.xp.query.expr;

import com.google.common.annotations.Beta;

@Beta
public final class DynamicOrderExpr
    extends OrderExpr
{
    private final FunctionExpr function;

    public DynamicOrderExpr( final FunctionExpr function, final Direction direction )
    {
        super( direction );
        this.function = function;
    }

    public FunctionExpr getFunction()
    {
        return this.function;
    }


    @Override
    public String toString()
    {
        return this.function + " " + getDirection();
    }
}
