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
package ch.petikoch.examples.mvvm_rxjava.example7;

import ch.petikoch.examples.mvvm_rxjava.datatypes.LogRow;
import ch.petikoch.examples.mvvm_rxjava.rxjava_mvvm.IView;
import ch.petikoch.examples.mvvm_rxjava.utils.GuiPreconditions;
import ch.petikoch.examples.mvvm_rxjava.widgets.StrictThreadingJFrame;
import com.google.common.collect.Lists;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableUtilities;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.Vector;

import static ch.petikoch.examples.mvvm_rxjava.rxjava_mvvm.RxViewModel2SwingViewBinder.bindViewModel;

class Example_7_View extends StrictThreadingJFrame implements IView<Example_7_ViewModel> {

    private final MyTableModel myTableModel = new MyTableModel();

    @Override
    public void bind(final Example_7_ViewModel viewModel) {
        bindViewModel(viewModel.vm2v_log).toAction(myTableModel::addRow);
    }

    public Example_7_View() {
        super();
        setTitle(getClass().getSimpleName() + " " + ManagementFactory.getRuntimeMXBean().getName());

        setBounds(100, 100, 700, 500);
        setDefaultCloseOperation(StrictThreadingJFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));

        final JXTable table = new JXTable(myTableModel);
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setSortable(false);
        table.getTableHeader().setReorderingAllowed(false);

        myTableModel.addTableModelListener(new TableModelListener() {

            int lastRowCountScrolledTo = -1;

            @Override
            public void tableChanged(final TableModelEvent e) {
                if (TableUtilities.isInsert(e)) {
                    final int currentRowCount = myTableModel.getRowCount();
                    if (currentRowCount != lastRowCountScrolledTo) {
                        lastRowCountScrolledTo = currentRowCount;
                        SwingUtilities.invokeLater(() -> table.scrollRectToVisible(table.getCellRect(myTableModel.getRowCount() - 1, 0, false)));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private static class MyTableModel extends DefaultTableModel {

        public MyTableModel() {
            super(new Vector<>(Lists.newArrayList("Timestamp", "Status", "Text")), 0);
            GuiPreconditions.assertOnAwtEdt();
        }

        public void addRow(LogRow logRow) {
            GuiPreconditions.assertOnAwtEdt();
            addRow(new Object[]{logRow.getTimestamp(), logRow.getStatus(), logRow.getText()});
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            GuiPreconditions.assertOnAwtEdt();
            return false;
        }
    }
}
