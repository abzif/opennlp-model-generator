import java.time.*;
import java.util.concurrent.*;

List<String> languageCodes = List.of(
    "cs", // czech
    "da", // danish
    "de", // german
    "el", // greek
    "en", // english
    "es", // spanish
    "fi", // finnish
    "fr", // french
    "he", // hebrew
    "it", // italian
    "ja", // japanese
    "ko", // korean
    "no", // norwegian
    "pl", // polish
    "pt", // portugal
    "ru", // russian
    "sv", // swedish
    "uk", // ukrainian
    "zh"); // chinese

List<String> modelMarkdownLines = new ArrayList<>();
modelMarkdownLines.add("# Pre-trained models");
modelMarkdownLines.add("");
modelMarkdownLines.add("---");

String javaOpts = System.getProperty("java.opts", "");
String toolsJar = System.getProperty("tools.jar", "");
Path workDirectory = Path.of(System.getProperty("work.dir", "."));
Path siteDirectory = Path.of(System.getProperty("site.dir", "."));
Integer maxExecMinutes = Integer.parseInt(System.getProperty("max.exec.minutes", "0"));



String getResourceDirectoryName(String languageCode, String normalizerType) {
    return String.format("%s-%s", languageCode, normalizerType);
}

String getModelFileName(String languageCode, String modelType) {
    return String.format("%s-%s.onlpm", languageCode, modelType);
}

String getReportFileName(String languageCode, String modelType) {
    return String.format("%s-%s.txt", languageCode, modelType);
}

String getLanguageName(String languageCode) {
    Locale l = Locale.forLanguageTag(languageCode);
    return l != null ? l.getDisplayLanguage(Locale.ENGLISH).toLowerCase() : "?";
}

String getPropertyValue(Path reportFile, String propertyName) throws Exception {
    String prefix = propertyName + "=";
    return Files.readAllLines(reportFile).stream()
            .filter(line -> line.startsWith(prefix))
            .map(line -> line.substring(prefix.length()).trim())
            .findFirst()
            .orElse("?");
}

String getTrainingSampleSizeInThousands(Path reportFile) throws Exception {
    try {
        int sampleSize = Integer.parseInt(getPropertyValue(reportFile, "Training-Sample-Size"));
        return String.format("%sk", sampleSize / 1000);
    } catch (NumberFormatException e) {
        return "?";
    }
}

String getTrainingAlgorithm(Path reportFile) throws Exception {
    return getPropertyValue(reportFile, "Training-Algorithm");
}

String getEvaluationScoreInPercent(Path reportFile) throws Exception {
    try {
        double evaluationScore = Double.parseDouble(getPropertyValue(reportFile, "Evaluation-Score")) * 100;
        return String.format("%.02f", evaluationScore);
    } catch (NumberFormatException e) {
        return "?";
    }
}

String createLanguageHeader(String languageCode) {
    return String.format("## %s", languageCode);
}

String createLanguageDescription(Path workDir, String languageCode) throws Exception {
    String languageName = getLanguageName(languageCode);
    String resourceDirName = getResourceDirectoryName(languageCode, "lucene");
    String reportFileName = getReportFileName(languageCode, "lemmatizer");
    Path reportPath = workDir.resolve(resourceDirName).resolve(reportFileName);
    String trainingSampleSize = getTrainingSampleSizeInThousands(reportPath);
    return String.format("language code: **%s**, language name: **%s**, training sample size: **%s**", languageCode, languageName, trainingSampleSize);
}

String createModelLine(Path workDir, String languageCode, String normalizerType, String modelType) throws Exception {
    String resourceDirName = getResourceDirectoryName(languageCode, normalizerType);
    String modelFileName = getModelFileName(languageCode, modelType);
    String reportFileName = getReportFileName(languageCode, modelType);
    Path reportPath = workDir.resolve(resourceDirName).resolve(reportFileName);
    String algorithm = getTrainingAlgorithm(reportPath);
    String score = getEvaluationScoreInPercent(reportPath);
    return String.format("- model file: **[%s](models/%s/%s)**, evaluation report: **[%s](models/%s/%s)**, training algorithm: **%s**, evaluation score: **%s**%%",
            modelFileName, resourceDirName, modelFileName,
            reportFileName, resourceDirName, reportFileName,
            algorithm, score);
}

void appendModelMarkdownLines(Path workDir, String languageCode, String normalizerType, List<String> modelMarkdownLines) throws Exception {
    modelMarkdownLines.add(createModelLine(workDir, languageCode, normalizerType, "sentence-detector"));
    modelMarkdownLines.add(createModelLine(workDir, languageCode, normalizerType, "tokenizer"));
    modelMarkdownLines.add(createModelLine(workDir, languageCode, normalizerType, "pos-tagger"));
    modelMarkdownLines.add(createModelLine(workDir, languageCode, normalizerType, "lemmatizer"));
}

void appendModelMarkdown(Path workDir, String languageCode, List<String> modelMarkdownLines) throws Exception {
    modelMarkdownLines.add("");
    modelMarkdownLines.add(createLanguageHeader(languageCode));
    modelMarkdownLines.add("");
    modelMarkdownLines.add(createLanguageDescription(workDir, languageCode));
    modelMarkdownLines.add("");
    appendModelMarkdownLines(workDir, languageCode, "lucene", modelMarkdownLines);
}

void copyModelAndReport(Path workDir, String languageCode, String normalizerType, String modelType, Path siteDir) throws Exception {
    String resourceDirName = getResourceDirectoryName(languageCode, normalizerType);
    String modelFileName = getModelFileName(languageCode, modelType);
    String reportFileName = getReportFileName(languageCode, modelType);
    Path srcModelFile = workDir.resolve(resourceDirName).resolve(modelFileName);
    Path dstModelFile = siteDir.resolve("resources").resolve("models").resolve(resourceDirName).resolve(modelFileName);
    Path srcReportFile = workDir.resolve(resourceDirName).resolve(reportFileName);
    Path dstReportFile = siteDir.resolve("resources").resolve("models").resolve(resourceDirName).resolve(reportFileName);
    Files.createDirectories(dstModelFile.getParent());
    Files.copy(srcModelFile, dstModelFile, StandardCopyOption.REPLACE_EXISTING);
    Files.copy(srcReportFile, dstReportFile, StandardCopyOption.REPLACE_EXISTING);
}

void copyModelDir(Path workDir, String languageCode, String normalizerType, Path siteDirectory) throws Exception {
    copyModelAndReport(workDir, languageCode, normalizerType, "sentence-detector", siteDirectory);
    copyModelAndReport(workDir, languageCode, normalizerType, "tokenizer", siteDirectory);
    copyModelAndReport(workDir, languageCode, normalizerType, "pos-tagger", siteDirectory);
    copyModelAndReport(workDir, languageCode, normalizerType, "lemmatizer", siteDirectory);
}

void copyModelFiles(Path workDir, String languageCode, Path siteDirectory) throws Exception {
    copyModelDir(workDir, languageCode, "lucene", siteDirectory);
}

Process startModelTrainProcess(Path workDir, String languageCode, String javaOpts, String jarFileName) throws Exception {
    ProcessBuilder builder = new ProcessBuilder("java", javaOpts, "-jar", jarFileName, languageCode);
    builder.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT);
    return builder.start();
}

void trainModel(Path workDir, String languageCode, String javaOpts, String jarFileName, LocalDateTime startTime) throws Exception {
    if (maxExecMinutes > 0) {
        var currentTime = LocalDateTime.now();
        var endTime = startTime.plusMinutes(maxExecMinutes);
        var timeoutMinutes = Duration.between(currentTime, endTime).toMinutes();
        var process = startModelTrainProcess(workDir, languageCode, javaOpts, jarFileName);
        if (process.waitFor(timeoutMinutes, TimeUnit.MINUTES)) {
            var exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IllegalStateException("model training failed");
            }
        } else {
            process.destroyForcibly();
            throw new InterruptedException("model training timeout");
        }
    } else {
        var process = startModelTrainProcess(workDir, languageCode, javaOpts, jarFileName);
        var exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("model training failed");
        }
    }
}

if (toolsJar.length() > 0) {
    try {
        var startTime = LocalDateTime.now();
        for (String languageCode : languageCodes) {
            try {
                trainModel(workDirectory, languageCode, javaOpts, toolsJar, startTime);
                copyModelFiles(workDirectory, languageCode, siteDirectory);
                appendModelMarkdown(workDirectory, languageCode, modelMarkdownLines);
            } catch (IllegalStateException e) {
                System.out.println(String.format("### Processing failed for language: '%s', ignoring ###", languageCode));
            }
        }
        Path modelMarkdownFile = siteDirectory.resolve("markdown").resolve("models.md");
        Files.createDirectories(modelMarkdownFile.getParent());
        Files.write(modelMarkdownFile, modelMarkdownLines);
    } catch (InterruptedException e) {
        System.out.println("### Processing lasted too long, aborting ###");
    }
}

/exit
