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

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class CowwebSearcher<V> {

    TableDefinition tableDefinition;

    /**
     * すべてのカラムの値を取得するかどうか（デフォルト: false）
     */
    boolean fetchAllColumns = false;

    private CowwebSearchStrategy strategy;

    private DataSource dataSource = CowwebDataSource.getInstance();

    // TODO: connection poolからsearcherのコネクションが取得できるようになったらこれを使う
    public static <T extends CowwebSearcher<?>> T getSearcher(Class<T> searcherType) {
        try {
            Constructor<T> constructor = searcherType.getConstructor();
            T searcher = constructor.newInstance();
            return searcher;
        } catch (NoSuchMethodException | SecurityException |
                InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(
                    "Failed to create a searcher instance using reflection.", e);
        }
    }

    // デフォルトはidのみ
    public CowwebSearcher<V> fetchAllAttributes(boolean fetchAllAttributes) {
        this.fetchAllColumns = fetchAllAttributes;
        return this;
    }

    public CowwebSearcher<V> setSearchStrategy(CowwebSearchStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public List<V> search() throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(buildQuery());
            resultSet = statement.executeQuery();
            return buildResult(resultSet);
        } catch (SQLException e) {
            // TODO: デバッグログ
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // do nothing.
                e.printStackTrace();
            }
        }
    }

    private String buildQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT");
        // column
        builder.append(" ");
        if (fetchAllColumns) {
            List<String> labels = tableDefinition.getAllColumnLabels();
            for (int i = 0; i < labels.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(labels.get(i));
            }
        } else {
            builder.append(tableDefinition.getIdColumnLabel());
        }
        // table name
        builder.append(" ");
        builder.append("FROM");
        builder.append(" ");
        builder.append(tableDefinition.getTableName());
        // search strategy
        builder.append(" ");
        builder.append("WHERE");
        builder.append(" ");
        builder.append(strategy.expand());
        System.out.println(builder.toString());
        return builder.toString();
    }

    abstract protected List<V> buildResult(ResultSet resultSet) throws SQLException;

}
