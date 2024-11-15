/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package opennlpmodgen.tools;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import lombok.SneakyThrows;
import opennlpmodgen.lucene.LuceneResourceDirectorySupplier;
import opennlpmodgen.lucene.LuceneTextNormalizer;
import opennlpmodgen.tools.util.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

@Configuration
@ComponentScan(basePackageClasses = MainConfig.class)
public class MainConfig {
    @Bean
    public WebClient webClient() {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        return new WebClient() {
            @Override
            @SneakyThrows
            public WebResponse makeGetRequest(URL url) {
                var request = HttpRequest.newBuilder().GET().uri(url.toURI()).build();
                var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                return new WebResponse(response.request().uri().toURL(), HashMap.ofAll(response.headers().map()).mapValues(vl -> vl.get(0)), Option.some(response.body()));
            }

            @Override
            @SneakyThrows
            public WebResponse makeHeadRequest(URL url) {
                var request = HttpRequest.newBuilder().method("HEAD", HttpRequest.BodyPublishers.noBody()).uri(url.toURI()).build();
                var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                return new WebResponse(response.request().uri().toURL(), HashMap.ofAll(response.headers().map()).mapValues(vl -> vl.get(0)), Option.none());
            }
        };
    }

    @Bean
    public RootDirectorySupplier rootDirectorySupplier() {
        return () -> {
            var rootDirectory = Path.of(System.getProperty("user.home")).resolve(".cache").resolve("opennlp-model-generator");
            return rootDirectory.toAbsolutePath().normalize();
        };
    }

    @Bean
    public ResourceDirectorySupplier resourceDirectorySupplier() {
        return new LuceneResourceDirectorySupplier(rootDirectorySupplier());
    }

    @Bean
    public TextNormalizer textNormalizer() {
        return new LuceneTextNormalizer();
    }
}
