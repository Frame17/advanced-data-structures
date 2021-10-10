import com.google.common.hash.BloomFilter
import com.google.common.hash.Funnels
import net.andreinc.mockneat.unit.user.Emails.emails
import java.nio.charset.Charset

@Suppress("UnstableApiUsage")
fun main() {
    val n = 1000
    val fpp = 0.01

    val guavaBloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), n, fpp)
    repeat(n) { guavaBloomFilter.put(genEmail()) }
    checkFpp(n) { guavaBloomFilter.mightContain(it) }

    val bloomFilter = BloomFilter<String>(n, fpp)
    repeat(n) { bloomFilter.put(genEmail()) }
    checkFpp(n) { bloomFilter[it] }
}

private fun checkFpp(n: Int, filter: (String) -> Boolean) =
    (0..n * 10_000).map { genEmail() }
        .count(filter)
        .let { println("False-positive probability: ${it.toDouble() / (n * 10_000)}") }

/**
 * Generating a random email address.
 * It is assumed that the probability of duplicate results is extremely low.
 */
private fun genEmail(): String = emails().domain("gmail.com").`val`()
