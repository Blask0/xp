package com.enonic.wem.core.index.query.function

import com.enonic.wem.api.query.expr.ValueExpr
import org.elasticsearch.index.query.QueryStringQueryBuilder
import spock.lang.Specification

class FulltextFunctionArgumentsTest
        extends Specification
{
    def "fulltext 3 arguments"()
    {
        given:
        def arguments = [ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "and" )]

        when:
        FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        then:
        functionArguments.getFieldName() == "myField"
        functionArguments.getSearchString() == "SearchString"
        functionArguments.getOperator() == QueryStringQueryBuilder.Operator.AND
    }

    def "fulltext 2 arguments"()
    {
        given:
        def arguments = [ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" )]

        when:
        FulltextFunctionArguments functionArguments = new FulltextFunctionArguments( arguments );

        then:
        functionArguments.getFieldName() == "myField"
        functionArguments.getSearchString() == "SearchString"
        functionArguments.getOperator() == QueryStringQueryBuilder.Operator.OR
    }


    def "fulltext 1 arguments"()
    {
        given:
        def arguments = [ValueExpr.string( "myField" )]

        when:
        new FulltextFunctionArguments( arguments );

        then:
        def exception = thrown( FunctionQueryBuilderException )
        exception.message.contains( 'Wrong number of arguments (1) for function \'fulltext\' (expected 2 to 3)' )

    }

    def "fulltext illegal operator argument"()
    {
        given:
        def arguments = [ValueExpr.string( "myField" ), ValueExpr.string( "SearchString" ), ValueExpr.string( "dummy" )]

        when:
        new FulltextFunctionArguments( arguments );

        then:
        def exception = thrown( FunctionQueryBuilderException )
        exception.message == 'Illegal argument \'DUMMY\' in function \'fulltext\', positon 3'
        exception.getCause().message == "No enum constant org.elasticsearch.index.query.QueryStringQueryBuilder.Operator.DUMMY"
    }
}
