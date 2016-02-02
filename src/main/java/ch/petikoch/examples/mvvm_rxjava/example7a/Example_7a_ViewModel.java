/**
 * Copyright (c) 2015-2016 Peti Koch
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
package ch.petikoch.examples.mvvm_rxjava.example7a;

import ch.petikoch.examples.mvvm_rxjava.datatypes.LogRow;
import ch.petikoch.examples.mvvm_rxjava.rxjava_mvvm.IViewModel;
import net.jcip.annotations.ThreadSafe;
import rx.subjects.BehaviorSubject;

@ThreadSafe
class Example_7a_ViewModel implements IViewModel<Example_7a_Model> {

    public final BehaviorSubject<LogRow> vm2v_log = BehaviorSubject.create();

    public Example_7a_ViewModel() {
        wireInternally();
    }

    private void wireInternally() {
        // NO-OP
    }

    @Override
    public void connectTo(final Example_7a_Model model) {
        model.getLogs()
                .doOnError(throwable -> vm2v_log.onNext(new LogRow("!", "Unexpected error -> will retry here in ViewModel", "Error: " + throwable.getMessage())))
                .retry(5)
                .onErrorReturn(throwable -> new LogRow("!!!", "Too many unexpected errors -> stop", "Error: " + throwable.getMessage()))
                .subscribe(vm2v_log);
    }

}
