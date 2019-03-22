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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.hhiroshell.cowweb.model.QaTableDefinition.Column;

public class QaSearcher extends CowwebSearcher<Qa> {

    public QaSearcher() {
        super();
        tableDefinition = QaTableDefinition.getInstance();
    }

    @Override
    protected List<Qa> buildResult(ResultSet resultSet) throws SQLException {
        List<Qa> qas = new ArrayList<>();
        while (resultSet.next()) {
            Qa.Builder builder = new Qa.Builder(resultSet.getString(tableDefinition.getIdColumnLabel()));
            Qa qa;
            if (fetchAllColumns) {
                qa = builder
                        .setA(resultSet.getString(Column.COL_A.getLabel()))
                        .Build();
            } else {
                qa = builder.Build();
            }
            qas.add(qa);
        }
        return qas;
    }

}