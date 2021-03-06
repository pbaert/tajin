/*
 * Copyright (C) 2011 Ovea <dev@ovea.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
describe("tajin", function () {

    it("is installed", function () {
        expect(tajin).not.toBe(undefined);
    });

    describe("tajin.modules()", function () {

        it("exposes available modules with tajin.modules()", function () {
            expect(tajin.modules().length).toBeGreaterThan(1);
        });

    });

    describe("tajin.configure()", function () {

        it("does nothing if called more than 1 time when succeed", function () {
            var c = 0;
            var m = {
                name: 'test',
                onconfigure: function () {
                    c++;
                }
            };
            tajin.install(m);
            expect(c).toBe(1);
            tajin.configure();
            expect(c).toBe(1);
        });

        it("calls each module exported onconfigure() method", function () {
            expect(tajin.options['test-module'].init_called).toBe(true);
        });

        it("exposes module exports and options and tajin to module init functions", function () {
            var t = new Tajin();
            t.configure({
                'module-2': {
                    myopt: 'myvalue'
                }
            });
            var passed = 0;
            var m = {
                name: 'module-2',
                onconfigure: function (tajin, opts) {
                    expect(opts.myopt).toBe('myvalue');
                    expect(t).toBe(tajin);
                    passed = 1;
                },
                exports: {
                    dummy: function () {
                    }
                }
            };
            t.install(m);
            expect(passed).toBe(1);
        });

        it("restart initialization at failure point when a module initialization fails", function () {
            var steps = [],
                m1 = {
                    name: 'mod1',
                    onconfigure: function () {
                        steps.push(1);
                    }
                }, m2 = {
                    name: 'mod2',
                    onconfigure: function () {
                        steps.push(2);
                    }
                }, failing = {
                    name: 'failing',
                    onconfigure: function () {
                        steps.push(3);
                        throw new Error('exception');
                    }
                },
                t = new Tajin(),
                call_err = false,
                call_succ = false,
                obj = {
                    err: function (t, e) {
                        call_err = true;
                        expect(e.message).toBe('exception');
                        expect(steps.length).toBe(2);
                        expect(steps[0]).toBe(1);
                        expect(steps[1]).toBe(3);
                    },
                    succ: function (t) {
                        call_succ = true;
                        expect(steps.length).toBe(3);
                        expect(steps[2]).toBe(2);
                    }
                };
            t.install(m1);
            t.install(failing);
            t.install(m2);
            expect(steps.length).toBe(0);
            try {
                t.configure({
                    onerror: obj.err,
                    onconfigure: obj.succ
                });
                // since no module is asynchrnous, the error is thrown in this part of code
                this.fail();
            } catch (e) {
                expect(e.message).toBe('exception');
            }
            waitsFor(function () {
                return call_err;
            }, 600, 'waiting for error');
            t.uninstall(failing);
            t.configure({
                onerror: obj.err,
                onconfigure: obj.succ
            });
            waitsFor(function () {
                return call_succ;
            }, 600, 'waiting for success');
            expect(steps.length).toBe(3);
        });

    });

    describe("tajin.install()", function () {

        it("installs a module", function () {
            tajin.install({name: 'module1'});
            expect(tajin.modules()).toContain('module1');
        });

        it("module onconfigure() method called at install time if tajin is initialized", function () {
            var c = 0;
            var m = {
                name: 'test',
                onconfigure: function () {
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
                    onconfigure: function () {
                        c1++;
                    }
                },
                m2 = {
                    name: 'test-m1',
                    onconfigure: function () {
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
