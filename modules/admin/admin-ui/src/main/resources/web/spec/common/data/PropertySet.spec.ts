import PropertyPath = api.data.PropertyPath;
import PropertyTree = api.data.PropertyTree;
import ValueTypes = api.data.ValueTypes;
import Value = api.data.Value;

describe("api.data.PropertySetTest", () => {

    describe("when addStrings", () => {

        it("given 2 string values then 2 properties are returned ", () => {
            var tree = new PropertyTree();
            var propertySet = tree.addPropertySet("mySet");
            var properties = tree.addStrings("myProp", ["1", "2"]);
            expect(properties.length).toBe(2);
            expect(properties[0].getString()).toBe("1");
            expect(properties[1].getString()).toBe("2");
        });
    });

    describe("when setProperty", () => {

        it("given ('myProp', 0) then not null is returned", () => {
            var tree = new PropertyTree();
            expect(tree.getRoot().setProperty("myProp", 0, new Value("myalue", ValueTypes.STRING))).not.toBeNull();
        });

        // TODO: Disabled: since toThrowError does not seem to work with PhantomJS
        xit("given ('myProp', 1) then Error is thrown", () => {
            var tree = new PropertyTree();
            var value = new Value("myalue", ValueTypes.STRING);
            var propertySet = tree.getRoot();
            expect(() => {
                propertySet.setProperty("myProp", 1, value);
            }).toThrowError("Index out of bounds: index: 1, size: 0");
        });
    });

    describe("when setPropertyByPath", () => {

        it("given (PropertyPath.fromString('myProp[0]')) then not null is returned", () => {
            var tree = new PropertyTree();
            expect(tree.getRoot().setPropertyByPath(PropertyPath.fromString("myProp[0]"),
                new Value("myalue", ValueTypes.STRING))).not.toBeNull();
        });

        it("given ('myProp[0]') then not null is returned", () => {
            var tree = new PropertyTree();
            var property = tree.getRoot().setPropertyByPath("myProp[0]", new Value("myalue", ValueTypes.STRING));
            expect(property).not.toBeNull();
        });

        // TODO: Disabled: since toThrowError does not seem to work with PhantomJS
        xit("given ('myProp[1]') then Error is thrown", () => {
            var tree = new PropertyTree();
            var value = new Value("myalue", ValueTypes.STRING);
            var propertySet = tree.getRoot();
            expect(() => {
                propertySet.setPropertyByPath("myProp[1]", value);
            }).toThrowError("Index out of bounds: index: 1, size: 0");
        });
    });

    describe("when getSize", () => {

        it("given a PropertySet with 3 properties when getSize returns 3", () => {
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            mySet.addPropertySet("subSet");

            expect(mySet.getSize()).toBe(3);
        });
    });

    describe("when removeProperty", () => {

        it("given a PropertySet with 3 properties when removing one then getTotalSize returns 2", () => {
            debugger;
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            mySet.addStrings("myProp", ["1", "2"]);
            var subSet = mySet.addPropertySet("subSet");
            subSet.addStrings("myProp", ["1", "2"]);

            mySet.removeProperty("myProp", 1);

            expect(mySet.getSize()).toBe(2);
        });
    });

    describe("when getProperty", () => {

        describe("given a PropertySet with a property named 'myProp'", () => {

            debugger;
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            var property = mySet.addProperty("myProp", new Value("myVal", ValueTypes.STRING));

            it("given name 'myProp' then not null is returned", () => {
                expect(mySet.getProperty("myProp")).toBe(property);
            });

            it("given name 'nonExisting' then null is returned", () => {
                expect(mySet.getProperty("nonExisting")).toBeNull();
            });

            it("given PropertyPath('myProp') then not null is returned", () => {
                expect(mySet.getProperty(PropertyPath.fromString("myProp"))).toBe(property);
            });

            it("given (name='myProp' and index=0) then not null is returned", () => {
                expect(mySet.getProperty("myProp", 0)).toBe(property);
            });

            it("given no arguments then the Property for the PropertySet is returned", () => {
                expect(mySet.getProperty()).toBe(tree.getProperty("mySet"));
            });
        });
    });

    describe("when forEach", () => {

        it("given two property arrays with two elements in each then iteration will iterate through all of them", () => {

            var properties = [];
            var tree = new PropertyTree();
            var mySet = tree.addPropertySet("mySet");
            properties.push(mySet.addString("a", "1"));
            properties.push(mySet.addString("a", "2"));
            properties.push(mySet.addString("b", "1"));
            properties.push(mySet.addString("b", "2"));

            mySet.forEach(function (property, index) {
                expect(property.getString()).toBe(properties[index].getString());
            });
        });
    });

});