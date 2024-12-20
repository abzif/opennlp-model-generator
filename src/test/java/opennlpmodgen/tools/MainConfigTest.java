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

import com.google.common.jimfs.Jimfs;
import opennlpmodgen.tools.MainConfigTest.XMainConfig;
import opennlpmodgen.tools.util.ResourceDirectorySupplier;
import opennlpmodgen.tools.util.RootDirectorySupplier;
import opennlpmodgen.tools.util.TextNormalizer;
import opennlpmodgen.tools.util.WebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {XMainConfig.class})
public class MainConfigTest {
    @Configuration
    public static class XMainConfig extends MainConfig {
        @Override
        @Bean
        public WebClient webClient() {
            return mock(WebClient.class);
        }

        @Override
        @Bean
        public RootDirectorySupplier rootDirectorySupplier() {
            return () -> Jimfs.newFileSystem().getPath("root");
        }

        @Override
        @Bean
        public ResourceDirectorySupplier resourceDirectorySupplier() {
            return language -> rootDirectorySupplier().getRootDirectory().resolve(String.format("%s-res", language));
        }

        @Override
        @Bean
        public TextNormalizer textNormalizer() {
            return (text, language) -> text;
        }
    }

    @Autowired
    private MainProcessor processor;

    @Test
    public void config() {
        assertThat(processor).isNotNull();
    }
}
