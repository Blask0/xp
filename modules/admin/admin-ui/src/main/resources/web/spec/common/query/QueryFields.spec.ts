import QueryFields = api.query.QueryFields;
import QueryField = api.query.QueryField;

describe("api.query.QueryFieldsTest", () => {

    describe("toString", () => {

        it("single QueryField", () => {
            var queryFields = new QueryFields();
            queryFields.add(new QueryField("test"));
            expect(queryFields.toString()).toBe("test");
        });

        it("more than one queryField", () => {
            var queryFields = new QueryFields();
            queryFields.add(new QueryField("test1"));
            queryFields.add(new QueryField("test2"));
            queryFields.add(new QueryField("test3"));
            expect(queryFields.toString()).toBe("test1,test2,test3");
        });

        it("with weigth", () => {
            var queryFields = new QueryFields();
            queryFields.add(new QueryField("test1", 5));
            queryFields.add(new QueryField("test2"));
            expect(queryFields.toString()).toBe("test1^5,test2");
        });

    });

});