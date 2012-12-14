describe("tajin", function () {

    it("is installed", function () {
        expect(tajin).not.toBe(undefined);
    });

    describe("tajin.modules()", function () {

        it("exposes available modules with tajin.modules()", function () {
            expect(tajin.modules().length).toBeGreaterThan(1);
        });

    });

    describe("tajin.init()", function () {

        it("does nothing if called more than 1 time when succeed", function () {
            var c = 0;
            var m = {
                name: 'test',
                init: function () {
                    c++;
                }
            };
            tajin.install(m);
            expect(c).toBe(1);
            tajin.init();
            expect(c).toBe(1);
        });

        it("calls each module exported init() method", function () {
            expect(tajin.options['test-module'].init_called).toBe(true);
        });

        it("restart initialization at failure point when a module initialization fails", function () {
            var steps = [];
            var m1 = {
                name: 'm1',
                init: function () {
                    steps.push('m1');
                }
            }, m2 = {
                name: 'm2',
                init: function () {
                    steps.push('m2');
                }
            }, failing = {
                name: 'failing',
                init: function () {
                    steps.push('failing');
                }
            };
            var t = new Tajin();
            t.install(m1);
            t.install(failing);
            t.install(m2);
            expect(steps).toBe([]);
            try {
                tajin.init();
                this.fail('should not go there');
            } catch (e) {
            }

        });

        it("exposes module exports, options and tajin to module's init functions", function () {
            var passed = 0;
            var m = {
                name: 'module-2',
                init: function (next, opts, tajin) {
                    expect(typeof next).toBe('function');
                    expect(typeof opts).toBe('object');
                    expect(opts.myopt).toBe('myvalue');
                    expect(window.tajin).toBe(tajin);
                    passed = 1;
                },
                exports: {
                    dummy: function () {
                    }
                }
            };
            tajin.install(m);
            expect(passed).toBe(1);
        });

    });

    describe("tajin.ready()", function () {

        it("is called when initialization is finished, and keep state", function () {
            var obj = {f: function () {
            }};
            spyOn(obj, 'f');
            tajin.ready(obj.f);
            expect(obj.f).toHaveBeenCalled();
        });

        it("optional callback method 'onready' called when initialization finished", function () {
            expect(window.onready_called).toBe(true);
        });

    });

    describe("tajin.install()", function () {

        it("installs a module", function () {
            tajin.install({name: 'module1'});
            expect(tajin.modules()).toContain('module1');
        });

        it("module init() method called at install time if tajin is initialized", function () {
            var c = 0;
            var m = {
                name: 'test',
                init: function () {
                    c++;
                }
            };
            tajin.install(m);
            expect(c).toBe(1);
        });

        it("check dependencies at installation time", function () {
            var m = {
                name: 'testdep',
                requires: 'inexisting'
            };
            expect(function () {
                tajin.install(m);
            }).toThrow(new Error("Error loading module 'testdep': missing modules: inexisting"));
        });

        it("check for required module.name attribute", function () {
            var m = {};
            expect(function () {
                tajin.install(m);
            }).toThrow(new Error("Module name is missing"));
        });

        it("overrides module previously installed with same name", function () {
            var c1 = 0, c2 = 0;
            var m1 = {
                    name: 'test-m1',
                    init: function () {
                        c1++;
                    }
                },
                m2 = {
                    name: 'test-m1',
                    init: function () {
                        c2++;
                    }
                };
            tajin.install(m1);
            expect(c1).toBe(1);
            expect(c2).toBe(0);
            tajin.install(m2);
            expect(c1).toBe(1);
            expect(c2).toBe(1);
        });

    });

    describe("tajin.uninstall()", function () {

        it("uninstalls a module", function () {
            tajin.install({name: 'module1'});
            tajin.install({name: 'module2'});

            expect(tajin.modules()).toContain('module1');
            expect(tajin.modules()).toContain('module2');

            tajin.uninstall('module1');

            expect(tajin.modules()).toContain('module2');
        });

        it("check for required name parameter", function () {
            expect(function () {
                tajin.uninstall();
            }).toThrow(new Error("Module name is missing"));
        });

    });
});
