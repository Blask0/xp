module api.content.query {

    export class ContentQuery {

        private queryExpr: api.query.expr.QueryExpr;

        private contentTypeNames: api.schema.content.ContentTypeName[] = [];

        private aggregationQueries: api.query.aggregation.AggregationQuery[] = [];

        private from: number = 0;

        private size: number = 10;

        setQueryExpr(queryExpr: api.query.expr.QueryExpr) {
            this.queryExpr = queryExpr;
        }

        getQueryExpr(): api.query.expr.QueryExpr {
            return this.queryExpr;
        }

        setContentTypeNames(contentTypeNames: api.schema.content.ContentTypeName[]) {
            this.contentTypeNames = contentTypeNames

        }

        getContentTypes(): api.schema.content.ContentTypeName[] {
            return this.contentTypeNames;
        }

        setFrom(from: number) {
            this.from = from;
        }

        getFrom(): number {
            return this.from;
        }

        setSize(size: number) {
            this.size = size;
        }

        getSize(): number {
            return this.size;
        }

        addAggregationQuery(aggregationQuery: api.query.aggregation.AggregationQuery) {
            this.aggregationQueries.push(aggregationQuery);
        }

        getAggregationQueries(): api.query.aggregation.AggregationQuery[] {
            return this.aggregationQueries;
        }

    }
}