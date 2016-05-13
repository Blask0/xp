module api.query {

    import ValueExpr = api.query.expr.ValueExpr;
    import FunctionExpr = api.query.expr.FunctionExpr;
    import DynamicConstraintExpr = api.query.expr.DynamicConstraintExpr;
    import LogicalExpr = api.query.expr.LogicalExpr;
    import LogicalOperator = api.query.expr.LogicalOperator;

    export class PathMatchExpression extends FulltextSearchExpression {

        static createWithPath(searchString: string, queryFields: QueryFields, path: string): api.query.expr.Expression {

            var expression = FulltextSearchExpression.create(searchString,queryFields);

            var args = [];
            args.push(ValueExpr.stringValue("_path"));
            args.push(ValueExpr.stringValue("/content"+path));

            var matchedExpr: FunctionExpr = new FunctionExpr("pathMatch", args);
            var matchedDynamicExpr: DynamicConstraintExpr = new DynamicConstraintExpr(matchedExpr);

            var booleanExpr: LogicalExpr = new LogicalExpr(expression, LogicalOperator.AND, matchedDynamicExpr);
            return booleanExpr;
        }
    }

    export class PathMatchExpressionBuilder extends FulltextSearchExpressionBuilder {

        path: string;

        addField(queryField: QueryField): PathMatchExpressionBuilder {
            super.addField(queryField);
            return this;
        }

        setSearchString(searchString: string): PathMatchExpressionBuilder {
            super.setSearchString(searchString);
            return this;
        }

        setPath(path: string): PathMatchExpressionBuilder {
            this.path = path;
            return this;
        }

        build(): api.query.expr.Expression {
            return PathMatchExpression.createWithPath(this.searchString, this.queryFields, this.path);
        }
    }

}

