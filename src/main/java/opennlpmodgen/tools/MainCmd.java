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

import opennlpmodgen.tools.util.RootDirectorySupplier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainCmd {
    public static void main(String[] args) {
        try (var ctx = new AnnotationConfigApplicationContext(MainConfig.class)) {
            if (args != null && args.length == 1) {
                var language = args[0];
                trainModels(ctx, language);
            } else {
                printHelp(ctx);
            }
        }
    }

    private static void trainModels(ApplicationContext ctx, String language) {
        var processor = ctx.getBean(MainProcessor.class);
        processor.processUniDepConlluModel(language);
    }

    private static void printHelp(ApplicationContext ctx) {
        var rootDir = ctx.getBean(RootDirectorySupplier.class).getRootDirectory();
        System.out.println("Provide <two-letter-language-code> to train models");
        System.out.println("<two-letter-language-code> - in example 'en', 'es', 'zh' etc");
        System.out.println("models will be generated to directory " + rootDir);
    }
}
