/*
 * Copyright (c) 2019 Hiroshi Hayakawa <hhiroshell@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.gr.java_conf.hhiroshell.cowweb.model;

import java.util.ArrayList;
import java.util.List;

public class QaTableDefinition implements TableDefinition {

    private static QaTableDefinition instance = null;

    public static QaTableDefinition getInstance() {
        if (instance == null) {
            instance = new QaTableDefinition();
        }
        return instance;
    }

    // uninstanciable
    private QaTableDefinition() {}

    static final String TABLE_NAME = "QA";

    static enum Column {

        COL_Q("Q"),
        COL_A("A"),
        ;

        private final String label;

        private Column(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }

    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdColumnLabel() {
        return Column.COL_Q.getLabel();
    }

    @Override
    public List<String> getAllColumnLabels() {
        Column[] columns = Column.values();
        List<String> labels = new ArrayList<>(columns.length);
        for (Column column : columns) {
            labels.add(column.getLabel());
        }
        return labels;
    }

}
