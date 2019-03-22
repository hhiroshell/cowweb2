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

package jp.gr.java_conf.hhiroshell.cowweb;

import com.github.ricksbrown.cowsay.Cowsay;
import io.helidon.metrics.RegistryFactory;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import jp.gr.java_conf.hhiroshell.cowweb.model.*;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;

import java.sql.SQLException;
import java.util.*;

public class CowsayService implements Service {

    private final MetricRegistry registry = RegistryFactory.getRegistryFactory().get()
            .getRegistry(MetricRegistry.Type.APPLICATION);
    private final Counter accessCounter = registry.counter("access_counter");

    private static final String br = System.getProperty("line.separator");

    private static final List<String> cowfiles;
    static {
//        cowfiles = Arrays.asList(new String[]{"default"});
        List<String> infelicities = Arrays.asList(new String[]{"head-in", "telebears", "sodomized"});
        List<String> c = new ArrayList<>();
        Arrays.stream(Cowsay.say(new String[]{"-l"}).split(br)).forEach(f -> {
            if (!infelicities.contains(f)) {
                c.add(f);
            }
        });
        cowfiles = Collections.unmodifiableList(c);
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::say);
    }

    private void say(ServerRequest request, ServerResponse response) {
        accessCounter.inc();
        String a;
        String q = request.queryParams().first("q").orElse(null);
        if (q == null || q.length() == 0) {
            a = "Moo!";
        } else {
            a = searchAnswer(q);
            if (a == null || a.length() == 0) {
                a = "Moo!";
            }
        }
        System.out.println(a);
        response.send(Cowsay.say(new String[]{"-f", getRandomCowfile(), a}));
    }

    private String searchAnswer(String q) {
        QaSearcher searcher = CowwebSearcher.getSearcher(QaSearcher.class);
        List<Qa> qas = null;
        try {
            qas = searcher
                    .fetchAllAttributes(true)
                    .setSearchStrategy(new SpecifyIdStrategy(QaTableDefinition.getInstance(), q))
                    .search();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (qas == null || qas.size() == 0) {
            return null;
        }
        return qas.get(0).getA();
    }

    private static String getRandomCowfile() {
        return cowfiles.get(new Random().nextInt(cowfiles.size()));
    }

}